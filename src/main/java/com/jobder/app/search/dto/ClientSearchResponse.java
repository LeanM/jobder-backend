package com.jobder.app.search.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientSearchResponse {
    private String id;
    @Email
    private String email;
    private String name;
    private Date birthDate;
    private String picture;
    private Double distanceInKm;
    private String clientProblemDescription;
    private String secretKey;
}