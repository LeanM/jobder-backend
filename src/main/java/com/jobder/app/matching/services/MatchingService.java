package com.jobder.app.matching.services;

import com.jobder.app.authentication.dto.userdtos.WorkerDTO;
import com.jobder.app.authentication.exceptions.InvalidClientException;
import com.jobder.app.authentication.exceptions.InvalidWorkerException;
import com.jobder.app.authentication.models.users.RoleName;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.chat.chatroom.ChatRoom;
import com.jobder.app.chat.chatroom.ChatRoomService;
import com.jobder.app.chat.exceptions.ChatRoomException;
import com.jobder.app.matching.dto.InteractionRequest;
import com.jobder.app.matching.dto.MatchRequest;
import com.jobder.app.matching.dto.ClientMatchesReponseDTO;
import com.jobder.app.matching.dto.WorkerMatchesResponseDTO;
import com.jobder.app.matching.exceptions.InvalidInteractionException;
import com.jobder.app.matching.models.Interaction;
import com.jobder.app.matching.models.InteractionState;
import com.jobder.app.matching.models.InteractionType;
import com.jobder.app.matching.repositories.InteractionRepository;
import com.jobder.app.search.dto.WorkerSearchResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;
    private final ChatRoomService chatRoomService;

    public void interactWithWorker(InteractionRequest interactionRequest) throws InvalidInteractionException {
        if(userRepository.existsById(interactionRequest.getClientId()) && userRepository.existsById(interactionRequest.getWorkerId())){
            validateInteractionWithWorker(interactionRequest);

            Interaction interaction = new Interaction();

            interaction.setWorkerId(interactionRequest.getWorkerId());
            interaction.setClientId(interactionRequest.getClientId());
            interaction.setClientUrgency(interactionRequest.getClientUrgency());
            interaction.setCreatedAt(new Date());

            if(interactionRequest.getInteractionType().equals(InteractionType.CLIENT_LIKE) || interactionRequest.getInteractionType().equals(InteractionType.CLIENT_DISLIKE)){
                interaction.setInteractionType(interactionRequest.getInteractionType());
            } else throw new InvalidInteractionException("Interaction type is invalid for this operation");

            if(interactionRequest.getClientProblemDescription() != null)
                interaction.setClientProblemDescription(interactionRequest.getClientProblemDescription());

            interactionRepository.save(interaction);

            if(interaction.getInteractionType().name().equals("CLIENT_LIKE"))
                chatRoomService.getChatRoomId(interactionRequest.getClientId(),interactionRequest.getWorkerId(), true);
        }
        else throw new InvalidInteractionException("Worker or Client doesnt exists!");
    }


    private void validateInteractionWithWorker(InteractionRequest interactionRequest) throws InvalidInteractionException {
        InteractionType interactionType = interactionRequest.getInteractionType();
        if(!interactionType.name().equals("CLIENT_LIKE") && !interactionType.name().equals("CLIENT_DISLIKE"))
            throw new InvalidInteractionException("Invalid interaction type!");

        Interaction interaction = interactionRepository.findInteractionByWorkerAndClient(interactionRequest.getWorkerId(),interactionRequest.getClientId());
        if(interaction != null)
            throw new InvalidInteractionException("Already exists an Interaction between worker and client!");

        User workerToInteract = userRepository.findById(interactionRequest.getWorkerId()).orElseThrow(() -> new InvalidInteractionException("Worker doesnt exist!"));
        if(!workerToInteract.getRole().name().equals("WORKER")){
            throw new InvalidInteractionException("Not a worker!");
        }
    }

    public void matchWithClient(MatchRequest matchRequest) throws InvalidInteractionException {
        if(userRepository.existsById(matchRequest.getClientId()) && userRepository.existsById(matchRequest.getWorkerId())){
            validateMatch(matchRequest);

            Interaction actualInteraction = interactionRepository.findInteractionByWorkerAndClient(matchRequest.getWorkerId(), matchRequest.getClientId());
            if( actualInteraction == null || !actualInteraction.getInteractionType().name().equals("CLIENT_LIKE") )
                throw new InvalidInteractionException("Doesnt exists previous interaction between users!");
            actualInteraction.setInteractionType(InteractionType.MATCH);
            actualInteraction.setCreatedAt(new Date());

            interactionRepository.save(actualInteraction);

            chatRoomService.setUnseenChatRoom(actualInteraction.getWorkerId(), actualInteraction.getClientId());
        }
        else throw new InvalidInteractionException("Worker or Client doesnt exists!");
    }

    private void validateMatch(MatchRequest matchRequest) throws InvalidInteractionException {
        Interaction existingInteraction = interactionRepository.findInteractionByWorkerAndClient(matchRequest.getWorkerId(), matchRequest.getClientId());
        if(existingInteraction != null && ( existingInteraction.getInteractionType().name().equals("CLIENT_DISLIKE") || existingInteraction.getInteractionType().name().equals("MATCH") ))
            throw new InvalidInteractionException("No previous valid interaction between client and worker!");
    }

    public void rejectClient(MatchRequest rejectClientRequest) throws InvalidInteractionException, ChatRoomException {
        if(userRepository.existsById(rejectClientRequest.getClientId()) && userRepository.existsById(rejectClientRequest.getWorkerId())){

            Interaction interaction = interactionRepository.findInteractionByWorkerAndClient(rejectClientRequest.getWorkerId(), rejectClientRequest.getClientId());
            if(interaction == null)
                throw new InvalidInteractionException("Doesnt exist previous interaction between client and worker!");

            interaction.setInteractionType(InteractionType.WORKER_REJECT);
            interactionRepository.save(interaction);

            chatRoomService.deleteChatRooms(rejectClientRequest.getClientId(), rejectClientRequest.getWorkerId());
        }
        else throw new InvalidInteractionException("Worker or Client doesnt exists!");
    }

    public void cancelMatch(MatchRequest cancelMatchRequest) throws InvalidInteractionException, ChatRoomException {
        if(userRepository.existsById(cancelMatchRequest.getClientId()) && userRepository.existsById(cancelMatchRequest.getWorkerId())){
            Interaction match = interactionRepository.findInteractionByWorkerAndClient(cancelMatchRequest.getWorkerId(), cancelMatchRequest.getClientId());
            if(match == null || !match.getInteractionType().name().equals("MATCH"))
                throw new InvalidInteractionException("Doenst exist a Match between worker and client!");
            match.setClosedAt(new Date());
            match.setInteractionType(InteractionType.MATCH_CANCELLED);
            interactionRepository.save(match);

            chatRoomService.deleteChatRooms(cancelMatchRequest.getClientId(), cancelMatchRequest.getWorkerId());
        }
        else throw new InvalidInteractionException("Worker or Client doesnt exists!");
    }

    public List<ClientMatchesReponseDTO> getClientMatchedOrLikedWorkers(String clientId) throws InvalidClientException, InvalidInteractionException {
        User client = userRepository.findById(clientId).orElseThrow(() -> new InvalidClientException("No client with that ID"));
        if(!client.getRole().name().equals("CLIENT")){
            throw new InvalidClientException("You are not a Client!");
        }

        List<Interaction> clientMatchesOrLikes = interactionRepository.findClientMatchesOrLikes(clientId);

        return fillClientMatchedOrLikedWorkers(clientMatchesOrLikes);
    }

    private List<ClientMatchesReponseDTO> fillClientMatchedOrLikedWorkers(List<Interaction> clientMatchesOrLikes) throws InvalidInteractionException {
        List<ClientMatchesReponseDTO> toReturn = new LinkedList<>();

        for (Interaction interaction : clientMatchesOrLikes){
            ClientMatchesReponseDTO clientMatchesReponseDTO = new ClientMatchesReponseDTO();
            ChatRoom chatRoomBetweenBoth = chatRoomService.getUserChatRoomWithOtherUser(interaction.getClientId(), interaction.getWorkerId());
            if(chatRoomBetweenBoth != null){
                clientMatchesReponseDTO.setChatRoom(chatRoomBetweenBoth);
                clientMatchesReponseDTO.setInteraction(interaction);
                User worker = userRepository.findById(interaction.getWorkerId()).orElse(null);
                if(worker != null){
                    clientMatchesReponseDTO.setUser(worker.toWorker());
                    toReturn.add(clientMatchesReponseDTO);
                } else {
                    //Borrar interaccion y chatroom
                }
            } else {
                //Crear chatroom?
            }
        }

        //Order by last message in chatroom
        Collections.sort(toReturn, new Comparator<>() {
            @Override
            public int compare(ClientMatchesReponseDTO o1, ClientMatchesReponseDTO o2) {
                return o2.getChatRoom().getLastMessageTimestamp().compareTo(o1.getChatRoom().getLastMessageTimestamp());
            }
        });

        return toReturn;
    }

    public List<WorkerMatchesResponseDTO> getWorkerLikedOrMatchedClients(String workerId) throws InvalidWorkerException {
        User worker = userRepository.findById(workerId).orElseThrow(()->new InvalidWorkerException("No Worker with that ID"));
        if(!worker.getRole().name().equals(RoleName.WORKER.name()))
            throw new InvalidWorkerException("You are not a Worker!");

        List<Interaction> workerMatchOrLikeInteractions = interactionRepository.findWorkerMatchesOrLikes(workerId);

        return fillWorkerMatchedOrLikedClients(workerMatchOrLikeInteractions);
    }

    private List<WorkerMatchesResponseDTO> fillWorkerMatchedOrLikedClients(List<Interaction> workerMatchOrLikeInteractions){
        List<WorkerMatchesResponseDTO> toReturn = new LinkedList<>();

        for (Interaction interaction : workerMatchOrLikeInteractions){
            WorkerMatchesResponseDTO workerMatchesReponseDTO = new WorkerMatchesResponseDTO();
            ChatRoom chatRoomBetweenBoth = chatRoomService.getUserChatRoomWithOtherUser(interaction.getWorkerId(), interaction.getClientId());
            if(chatRoomBetweenBoth != null){
                workerMatchesReponseDTO.setChatRoom(chatRoomBetweenBoth);
                workerMatchesReponseDTO.setInteraction(interaction);
                User client = userRepository.findById(interaction.getClientId()).orElse(null);
                if(client != null){
                    workerMatchesReponseDTO.setUser(client.toClient());
                    toReturn.add(workerMatchesReponseDTO);
                }
                else{
                    //Borrar interaccion y chatroom
                }
            } else {
                //Crear chatroom?
            }
        }

        //Order by last message in chatroom
        Collections.sort(toReturn, new Comparator<>() {
            @Override
            public int compare(WorkerMatchesResponseDTO o1, WorkerMatchesResponseDTO o2) {
                return o2.getChatRoom().getLastMessageTimestamp().compareTo(o1.getChatRoom().getLastMessageTimestamp());
            }
        });

        return toReturn;
    }

    public List<WorkerSearchResponse> validateWorkers(User client, List<WorkerSearchResponse> workersToValidate){
        List<WorkerSearchResponse> validatedWorkers = new LinkedList<>();
        for(WorkerSearchResponse workerToValidate : workersToValidate){
            if(!interactionRepository.existsByClientIdAndWorkerId(client.getId(),workerToValidate.getWorker().getId())){
                //Si no poseen una interaccion
                validatedWorkers.add(workerToValidate);
            }
        }

        return validatedWorkers;
    }

    public boolean existsMatchBetweenUsers(String clientId, String workerId) {
        return interactionRepository.existsMatchByClientIdAndWorkerId(clientId, workerId);
    }
}
