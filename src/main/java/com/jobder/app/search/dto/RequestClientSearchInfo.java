package com.jobder.app.search.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestClientSearchInfo {
    private String id;
    private String professionName;
    private Integer minimumDistanceInKm;
    private Double latitude;
    private Double longitude;
}
