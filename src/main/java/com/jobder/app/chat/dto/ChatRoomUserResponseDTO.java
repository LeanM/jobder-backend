package com.jobder.app.chat.dto;

import com.jobder.app.authentication.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUserResponseDTO {
    private String chatRoomState;
    private User user;
}
