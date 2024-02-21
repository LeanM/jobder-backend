package com.jobder.app.review.dto;

import com.jobder.app.review.models.Review;
import com.jobder.app.review.models.ReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
    private Review review;
    private ReviewResponse reviewResponse;
}
