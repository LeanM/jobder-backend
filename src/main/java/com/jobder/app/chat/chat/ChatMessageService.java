package com.jobder.app.chat.chat;


import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.chat.chatroom.ChatRoom;
import com.jobder.app.chat.chatroom.ChatRoomService;
import com.jobder.app.chat.dto.ChatRoomUserResponseDTO;
import com.jobder.app.chat.exceptions.ChatRoomException;
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

    public ChatMessage save(ChatMessage chatMessage) throws ChatRoomException {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), false)
                .orElseThrow(()->new ChatRoomException("Users doenst have a chat room between them!")); // You can create your own dedicated exception
        chatMessage.setChatId(chatId);
        chatMessage.setSeenByRecipient(false);
        chatRoomService.updateChatRoomOnMessage(repository.save(chatMessage));

        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        chatRoomService.setSeenChatRoomOnOpenChat(senderId, recipientId);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }

    public List<ChatMessage> findNotSeenChatMessages(String senderId, String recipientId) throws ChatRoomException {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        chatRoomService.setSeenChatRoomOnOpenChat(senderId, recipientId);
        if(!chatId.isPresent())
            throw new ChatRoomException("No exists chatroom between users!");
        List<ChatMessage> notSeenMessages = repository.findByChatIdAndNotSeenByRecipient(chatId.get(), senderId);

        for(ChatMessage message : notSeenMessages){
            message.setSeenByRecipient(true);
            //repository.save(message);
        }

        return notSeenMessages;
    }
}