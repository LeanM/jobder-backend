package com.jobder.app.chat.dto;

import com.jobder.app.authentication.models.users.User;
import com.jobder.app.chat.chatroom.ChatRoomState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUserResponseDTO {
    private ChatRoomState chatRoomState;
    private User user;
}
