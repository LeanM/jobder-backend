package com.jobder.app.matching.repositories;

import com.jobder.app.matching.models.Interaction;
import com.jobder.app.matching.models.InteractionType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InteractionRepository extends MongoRepository<Interaction, String> {

    @Query("{ 'workerId' : ?0 , 'clientId' : ?1 }")
    Interaction findInteractionByWorkerAndClient(String workerId, String clientId);

    boolean existsByClientIdAndWorkerId(String clientId, String workerId);

    @Query("{ 'workerId' : ?0 , 'interactionType' : ?1 }")
    List<Interaction> findWorkerTypeInteractions(String workerId, InteractionType interactionType);

    @Query("{ 'clientId' : ?0 , 'interactionType' : ?1 }")
    List<Interaction> findClientTypeInteractions(String clientId, InteractionType interactionType);

}
