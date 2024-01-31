package com.jobder.app.chat.chat;


import com.jobder.app.authentication.models.User;
import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.chat.chatroom.ChatRoom;
import com.jobder.app.chat.chatroom.ChatRoomService;
import com.jobder.app.chat.dto.ChatRoomUserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), false)
                .orElseThrow(); // You can create your own dedicated exception
        chatMessage.setChatId(chatId);
        chatRoomService.setUnseenChatRoomOnMessage(chatMessage.getSenderId(),chatMessage.getRecipientId());
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        chatRoomService.setSeenChatRoomOnOpenChat(senderId, recipientId);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }

    public List<ChatRoomUserResponseDTO> findChatUsers(String userId) {
        List<ChatRoom> userChatRooms = chatRoomService.getUserChatRooms(userId);
        List<ChatRoomUserResponseDTO> chatRoomUserResponseDTOS = new LinkedList<>();

        for(ChatRoom chatRoom : userChatRooms){
            if(userRepository.existsById(chatRoom.getRecipientId())) {
                Optional<User> user = userRepository.findById(chatRoom.getRecipientId());
                ChatRoomUserResponseDTO chatRoomUserResponseDTO = new ChatRoomUserResponseDTO();
                chatRoomUserResponseDTO.setChatRoomState(chatRoom.getState());
                chatRoomUserResponseDTO.setUser(user.orElseThrow());
                chatRoomUserResponseDTOS.add(chatRoomUserResponseDTO);
            }
        }

        return chatRoomUserResponseDTOS;
    }
}