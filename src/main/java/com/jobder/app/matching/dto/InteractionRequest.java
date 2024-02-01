package com.jobder.app.matching.dto;

import com.jobder.app.matching.models.InteractionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractionRequest {
    private String clientId;
    @NonNull
    private String workerId;
    @NonNull
    private InteractionType interactionType;
    private String clientProblemDescription;
}
