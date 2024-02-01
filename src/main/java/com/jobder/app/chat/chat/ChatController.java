package com.jobder.app.chat.chat;

import com.jobder.app.authentication.models.User;
import com.jobder.app.chat.dto.ChatRoomUserResponseDTO;
import lombok.RequiredArgsConstructor;
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

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        System.out.println("ASDAS");
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
    }

    @GetMapping("/messages/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@AuthenticationPrincipal User user,
                                                              @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(user.getId(), recipientId));
    }
    /*

    @GetMapping("/chatusers/{userId}")
    public ResponseEntity<List<ChatRoomUserResponseDTO>> findChatUsers(@PathVariable String userId){
        return ResponseEntity
                .ok(chatMessageService.findChatUsers(userId));
    }
     */
}
