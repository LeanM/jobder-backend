package com.jobder.app.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchParameters {
    private String clientProblemDescription;
    private String workSpecialization;
    private AvailabilityStatus availabilityStatus;
}
