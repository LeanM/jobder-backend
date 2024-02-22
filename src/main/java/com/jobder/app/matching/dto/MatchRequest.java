package com.jobder.app.matching.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequest {
    private String workerId;
    private String clientId;
}