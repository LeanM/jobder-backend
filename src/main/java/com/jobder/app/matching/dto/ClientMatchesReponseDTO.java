package com.jobder.app.matching.dto;

import com.jobder.app.authentication.dto.WorkerDTO;
import com.jobder.app.authentication.models.User;
import com.jobder.app.chat.chatroom.ChatRoom;
import com.jobder.app.matching.models.Interaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientMatchesReponseDTO {
    private ChatRoom chatRoom;
    private Interaction interaction;
    private WorkerDTO worker;
}
