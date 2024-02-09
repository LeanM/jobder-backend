package com.jobder.app.chat.chat;

import com.jobder.app.authentication.exceptions.InvalidAuthException;
import com.jobder.app.authentication.models.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final PushNotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<?> processMessage(@RequestBody ChatMessage chatMessage, @AuthenticationPrincipal User user) {
        //Hacer este metodo http autenticando al usuario y verificando que posea un match o chatroom
        chatMessage.setSenderId(user.getId());
        ResponseEntity<?> response;
        try{
            response = new ResponseEntity<>(chatMessageService.save(chatMessage), HttpStatus.OK);
        }catch(InvalidAuthException e){
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping("/messages/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@AuthenticationPrincipal User user,
                                                              @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(user.getId(), recipientId));
    }

    @GetMapping("/suscribe")
    public Flux<ServerSentEvent<List<ChatMessage>>> openUserConnection(@AuthenticationPrincipal User user) {
        return notificationService.getNotificationsByRecipientID(user.getId());
    }

    /*

    @GetMapping("/chatusers/{userId}")
    public ResponseEntity<List<ChatRoomUserResponseDTO>> findChatUsers(@PathVariable String userId){
        return ResponseEntity
                .ok(chatMessageService.findChatUsers(userId));
    }
     */
}
