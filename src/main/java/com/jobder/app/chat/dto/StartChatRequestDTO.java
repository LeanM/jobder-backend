package com.jobder.app.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartChatRequestDTO {
    private String senderId;
    private String recipientId;
}
