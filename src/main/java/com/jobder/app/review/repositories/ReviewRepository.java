package com.jobder.app.review.repositories;

import com.jobder.app.matching.models.Interaction;
import com.jobder.app.matching.models.InteractionType;
import com.jobder.app.review.models.Review;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    @Query("{ 'workerId' : ?0  }")
    List<Review> findByWorkerId(String workerId);

    @Aggregation(pipeline = {
            "{ '$match': { 'workerId' : ?0 } }",
            "{ '$limit' : ?1 }",
    })
    List<Review> findByWorkerIdAndLimit(String workerId, int limit);
}
