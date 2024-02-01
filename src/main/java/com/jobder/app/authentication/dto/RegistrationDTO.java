package com.jobder.app.authentication.dto;

import com.jobder.app.authentication.models.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    private RoleName accountRole;
    @NonNull
    private String value;

    //Common client and worker
    private String name;
    private String picture;
    private String email;
    private String phoneNumber;
    private String address;
    private Double latitude;
    private Double longitude;
    private Date birthDate;

    //Worker
    private String workSpecialization;
    private String description;
    private String workingHours;
}