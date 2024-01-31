package com.jobder.app.chat.chatroom;

import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.chat.chat.ChatMessage;
import com.jobder.app.chat.chat.ChatMessageRepository;
import com.jobder.app.chat.exceptions.ChatRoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public Optional<String> getChatRoomId(
            String senderId,
            String recipientId,
            boolean createNewRoomIfNotExists
    ) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId,recipientId);
        Optional<String> chatIdentifier = chatRoom.map(ChatRoom::getChatId);
        return chatIdentifier
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }

                    return  Optional.empty();
                });
    }

    private String createChatId(String senderId, String recipientId) {
        //chequear que el recipient exista y sea worker
        var chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .state(ChatRoomState.NEW)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .state(ChatRoomState.NEW)
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }

    public List<ChatRoom> getUserChatRooms(String userId){
        return chatRoomRepository.findBySenderId(userId);
    }

    public void setUnseenChatRoomOnMessage(String senderId, String recipientId){
        //Busco el chatroom del usuario que recibe el mensaje
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(recipientId,senderId);
        chatRoom.ifPresent(chatRoom1 -> {chatRoom1.setState(ChatRoomState.UNSEEN);chatRoomRepository.save(chatRoom1);});
    }

    public void setSeenChatRoomOnOpenChat(String openerId, String recipientId){
        //Marco el chatroom como visto ya que se solicitaron los mensajes
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(openerId,recipientId);
        chatRoom.ifPresent(chatRoom1 -> {chatRoom1.setState(ChatRoomState.SEEN);chatRoomRepository.save(chatRoom1);});
    }
    public void deleteChatRooms(String senderId, String recipientId) throws ChatRoomException {
        Optional<ChatRoom> firstChatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId,recipientId);
        Optional<ChatRoom> secondChatRoom = chatRoomRepository.findBySenderIdAndRecipientId(recipientId,senderId);

        String chatId = firstChatRoom.map(ChatRoom::getChatId).orElseThrow(()-> new ChatRoomException("Doesnt exist chatroom between users!"));

        firstChatRoom.ifPresent(chatRoomRepository::delete);
        secondChatRoom.ifPresent(chatRoomRepository::delete);

        deleteChatRoomMessages(chatId);
    }

    private void deleteChatRoomMessages(String chatId){
        List<ChatMessage> roomMessages = chatMessageRepository.findByChatId(chatId);

        chatMessageRepository.deleteAll(roomMessages);
    }
}