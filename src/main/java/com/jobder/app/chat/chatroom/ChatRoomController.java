package com.jobder.app.chat.chatroom;

import com.jobder.app.authentication.models.users.User;
import com.jobder.app.chat.dto.StartChatRequestDTO;
import com.jobder.app.chat.exceptions.ChatRoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin("*")
@RequestMapping("/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/startChat")
    public ResponseEntity createChatRoom(@AuthenticationPrincipal User user, @RequestBody StartChatRequestDTO chatRequestDTO){
        return ResponseEntity.ok(chatRoomService.getChatRoomId(user.getId(), chatRequestDTO.getRecipientId(), true));
    }
    @PostMapping("/closeChat")
    public ResponseEntity closeChatRoom(@RequestBody StartChatRequestDTO chatRequestDTO){
        ResponseEntity responseEntity;
        try{
            chatRoomService.deleteChatRooms(chatRequestDTO.getSenderId(), chatRequestDTO.getRecipientId());
            responseEntity = new ResponseEntity("Deleted Chatrooms", HttpStatus.OK);
        }
        catch (ChatRoomException e){
            responseEntity = new ResponseEntity("Problem deleting chatrooms", HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }
}
