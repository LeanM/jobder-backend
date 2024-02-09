package com.jobder.app.chat.chat;


import com.jobder.app.authentication.exceptions.InvalidAuthException;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.chat.chatroom.ChatRoom;
import com.jobder.app.chat.chatroom.ChatRoomService;
import com.jobder.app.chat.dto.ChatRoomUserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.Message;
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
    private final ChatNotificationRepository notificationRepository;

    public ChatMessage save(ChatMessage chatMessage) throws InvalidAuthException {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), false)
                .orElseThrow(()-> new InvalidAuthException("No Chatroom between users!")); // You can create your own dedicated exception
        chatMessage.setChatId(chatId);

        ChatNotification chatNotification = new ChatNotification();
        chatNotification.setMessage(repository.save(chatMessage));
        chatNotification.setDelivered(false);
        chatNotification.setToUserId(chatMessage.getRecipientId());
        notificationRepository.save(chatNotification);

        chatRoomService.setUnseenChatRoomOnMessage(chatMessage.getSenderId(),chatMessage.getRecipientId());

        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        chatRoomService.setSeenChatRoomOnOpenChat(senderId, recipientId);

        //Colocar todos los mensajes como delivered
        List<ChatMessage> chatMessages = chatId.map(repository::findByChatId).orElse(new ArrayList<>());

        return chatMessages;
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