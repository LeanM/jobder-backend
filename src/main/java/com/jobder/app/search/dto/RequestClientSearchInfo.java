package com.jobder.app.search.dto;

import com.jobder.app.authentication.models.AvailabilityStatus;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
