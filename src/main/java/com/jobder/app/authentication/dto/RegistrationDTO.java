package com.jobder.app.authentication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jobder.app.authentication.models.users.RoleName;
import com.jobder.app.authentication.models.users.SearchParameters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    private RoleName accountRole;
    private String value;
    private Boolean isGoogleRegister;

    //Common client and worker
    private String name;
    private String password;
    private String picture;
    private String email;
    private String phoneNumber;
    private String address;
    private Double latitude;
    private Double longitude;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthDate;

    //Worker
    private String workSpecialization;
    private String description;
    private String workingHours;

    //Client
    private SearchParameters searchParameters;
}