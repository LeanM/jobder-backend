package com.jobder.app.chat.chat;

import com.jobder.app.authentication.models.users.User;
import com.jobder.app.chat.chatroom.ChatRoomService;
import com.jobder.app.chat.exceptions.ChatRoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        //Realizar autenticacion al enviar un mensaje?
        try{
            ChatMessage savedMsg = chatMessageService.save(chatMessage);
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipientId(), "/queue/messages",
                    new ChatNotification(
                            savedMsg.getId(),
                            savedMsg.getSenderId(),
                            savedMsg.getRecipientId(),
                            savedMsg.getContent(),
                            savedMsg.getTimestamp()
                    )
            );
        }catch (ChatRoomException e){
            System.out.println(e.getMessage());
        }


    }

    @GetMapping("/messages/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@AuthenticationPrincipal User user,
                                                              @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(user.getId(), recipientId));
    }

    @GetMapping("/messages/unseen/{recipientId}")
    public ResponseEntity<?> findNotSeenChatMessages(@AuthenticationPrincipal User user,
                                                                     @PathVariable String recipientId) {
        try{
            return ResponseEntity
                    .ok(chatMessageService.findNotSeenChatMessages(user.getId(), recipientId));
        } catch (ChatRoomException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}
