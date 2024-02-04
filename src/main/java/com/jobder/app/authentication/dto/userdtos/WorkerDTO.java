package com.jobder.app.authentication.dto.userdtos;

import com.jobder.app.authentication.models.AvailabilityStatus;
import com.jobder.app.authentication.models.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDTO extends UserDTO {
    private String workSpecialization;
    private AvailabilityStatus availabilityStatus;
    private String description;
    private String workingHours;
    private Float averageRating;
    private Integer worksFinished;

    public WorkerDTO(CommonDataDTO commonDataDTO, String workSpecialization, AvailabilityStatus availabilityStatus, String description, String workingHours, Float averageRating, Integer worksFinished){
        super(commonDataDTO);
        this.workSpecialization = workSpecialization;
        this.availabilityStatus = availabilityStatus;
        this.description = description;
        this.workingHours = workingHours;
        this.averageRating = averageRating;
        this.worksFinished = worksFinished;
    }
}
