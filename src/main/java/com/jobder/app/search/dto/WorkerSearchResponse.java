package com.jobder.app.search.dto;


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
    private String id;
    @Email
    private String email;
    private String name;
    private String description;
    private String professionName;
    private Date birthDate;
    private Float averageRating;
    private String picture;
    private Double distanceInKm;
    private String secretKey;
}
