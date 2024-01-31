package com.jobder.app.search.dto;


import com.jobder.app.authentication.dto.WorkerDTO;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerSearchResponse {
    private WorkerDTO worker;
    private Double distanceInKm;
    private String secretKey;
}
