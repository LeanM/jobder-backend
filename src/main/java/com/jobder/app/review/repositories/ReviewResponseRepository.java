package com.jobder.app.review.repositories;

import com.jobder.app.matching.models.Interaction;
import com.jobder.app.review.models.ReviewResponse;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ReviewResponseRepository extends MongoRepository<ReviewResponse, String> {

    @Aggregation(pipeline = {
            "{ '$match': { 'reviewId' : ?0 } }",
            "{ '$limit' : 1 }",
    })
    Optional<ReviewResponse> findByReviewId(String reviewId);
}
