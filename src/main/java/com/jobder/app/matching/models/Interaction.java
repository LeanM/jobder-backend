package com.jobder.app.matching.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Interaction {
    @Id
    private String id;
    private String clientId;
    private String workerId;
    @NonNull
    private InteractionType interactionType;
    private InteractionState interactionState;
    private String clientProblemDescription;

    private Date createdAt;
    private Date closedAt;
}
