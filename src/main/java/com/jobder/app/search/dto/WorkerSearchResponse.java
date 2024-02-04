package com.jobder.app.search.dto;


import com.jobder.app.authentication.dto.userdtos.WorkerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerSearchResponse {
    private WorkerDTO worker;
    private Double distanceInKm;
    private String secretKey;
}
