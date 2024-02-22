package com.jobder.app.matching.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.jobder.app.authentication.models.users.AvailabilityStatus;
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
    private String clientProblemDescription;
    private AvailabilityStatus clientUrgency;

    private Date createdAt;
    private Date closedAt;
}
