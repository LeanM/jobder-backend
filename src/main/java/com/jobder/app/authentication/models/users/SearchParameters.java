package com.jobder.app.authentication.models.users;

import com.jobder.app.authentication.models.users.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchParameters {
    private String clientProblemDescription;
    private String workSpecialization;
    private AvailabilityStatus availabilityStatus;
}
