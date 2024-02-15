package com.jobder.app.chat.chatroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeenChatRoomRequestDTO {
    private String senderId;
    private String recipientId;
}
