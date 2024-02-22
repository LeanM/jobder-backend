package com.jobder.app.authentication.dto.userdtos;

import com.jobder.app.authentication.models.users.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDTO extends UserDTO {
    private String workSpecialization;
    private AvailabilityStatus availabilityStatus;
    private String description;
    private String workingHours;
    private String averageRating;
    private Integer worksFinished;
    private Integer totalReviews;

    public WorkerDTO(CommonDataDTO commonDataDTO, String workSpecialization, AvailabilityStatus availabilityStatus, String description, String workingHours, String averageRating, Integer worksFinished, Integer totalReviews){
        super(commonDataDTO);
        this.workSpecialization = workSpecialization;
        this.availabilityStatus = availabilityStatus;
        this.description = description;
        this.workingHours = workingHours;
        this.averageRating = averageRating;
        this.worksFinished = worksFinished;
        this.totalReviews = totalReviews;
    }
}
