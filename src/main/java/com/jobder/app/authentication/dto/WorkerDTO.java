package com.jobder.app.authentication.dto;

import com.jobder.app.authentication.models.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDTO {

    private String id;
    private String name;
    private String email;
    private String picture;
    private String phoneNumber;
    private String address;
    private Double latitude;
    private Double longitude;
    private Date birthDate;
    private String workSpecialization;
    private String availabilityStatus;
    private String description;
    private String workingHours;
    private String averageRating;
    private Integer worksFinished;
}
