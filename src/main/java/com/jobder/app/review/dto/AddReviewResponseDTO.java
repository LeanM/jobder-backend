package com.jobder.app.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddReviewResponseDTO {
    private String reviewId;
    private String workerId;
    private String content;
    private Float rating;
}
