package com.jobder.app.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestWorkerReviewsDTO {
    private String workerId;
    @NonNull
    private Integer pageNumber;
}
