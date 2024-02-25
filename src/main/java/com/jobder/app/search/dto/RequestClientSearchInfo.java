package com.jobder.app.search.dto;

import com.jobder.app.authentication.models.users.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestClientSearchInfo {
    private String id;
    private String workSpecialization;
    private Integer minimumDistanceInKm;
    private String clientProblemDescription;
    private AvailabilityStatus availabilityStatus;
    private Double latitude;
    private Double longitude;
    @NonNull
    private Integer initialPage;
}
