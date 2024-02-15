package com.jobder.app.chat.chatroom;

import com.jobder.app.authentication.models.users.User;
import com.jobder.app.chat.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping( path = "chatroom" )
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/updateSeenChatroom")
    public ResponseEntity<?> setVisitedMessages(@AuthenticationPrincipal User user, @RequestBody SeenChatRoomRequestDTO seenChatRoomRequestDTO){
        seenChatRoomRequestDTO.setSenderId(user.getId());


        return null;
    }
}
