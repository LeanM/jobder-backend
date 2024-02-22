package com.jobder.app.matching.dto;

import com.jobder.app.authentication.models.users.User;
import com.jobder.app.matching.models.Interaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchesCompletedResponseDTO {
    private User user;
    private Interaction interaction;
}
