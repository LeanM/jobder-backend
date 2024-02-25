package com.jobder.app.matching.repositories;

import com.jobder.app.matching.models.Interaction;
import com.jobder.app.matching.models.InteractionType;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InteractionRepository extends MongoRepository<Interaction, String> {

    @Query("{ 'workerId' : ?0 , 'clientId' : ?1 }")
    Interaction findInteractionByWorkerAndClient(String workerId, String clientId);

    @ExistsQuery("{ 'clientId' : ?0 , 'workerId' : ?1 , '$or':[ {'interactionType' : 'MATCH' }, {'interactionType' : 'CLIENT_LIKE' } , {'interactionType' : 'CLIENT_DISLIKE' }, {'interactionType' : 'WORKER_REJECT' }, {'interactionType' : 'MATCH_COMPLETED' } ] }")
    boolean existsCurrentInteractionByClientIdAndWorkerId(String clientId, String workerId);

    @ExistsQuery("{ 'clientId' : ?0 , 'workerId' : ?1 , 'interactionType' : 'MATCH_COMPLETED' }")
    boolean existsMatchCompletedByClientIdAndWorkerId(String clientId, String workerId);

    @Query("{ 'workerId' : ?0 , '$or':[ {'interactionType' : 'MATCH' }, {'interactionType' : 'CLIENT_LIKE' } ] }")
    List<Interaction> findWorkerMatchesOrLikes(String workerId);

    @Query("{ 'clientId' : ?0 , '$or':[ {'interactionType' : 'MATCH' }, {'interactionType' : 'CLIENT_LIKE' } ] }")
    List<Interaction> findClientMatchesOrLikes(String clientId);

    @Query("{ 'clientId' : ?0 , 'interactionType' : 'MATCH_COMPLETED' } }")
    List<Interaction> findClientCompletedMatches(String clientId);
}
