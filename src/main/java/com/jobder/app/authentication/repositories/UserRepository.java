package com.jobder.app.authentication.repositories;

import com.jobder.app.authentication.models.users.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    String HAVERSINE_FORMULA = "(6371 * acos(cos(radians(:latitude)) * cos(radians(w.latitude)) *" +
            " cos(radians(w.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(w.latitude))))";


    //@Query("SELECT w FROM User w WHERE w.role = WORKER AND " + HAVERSINE_FORMULA + " < :mindistance ORDER BY w.averageRating DESC")
    @Query("{ 'role' : 'WORKER', '$or': [{'availabilityStatus' : 'AVAILABLE'}, {'availabilityStatus' : 'MODERATED'}] }")
    List<User> findWorkers(Pageable pageable);

    @Query("{ 'role' : 'WORKER', 'availabilityStatus' : 'AVAILABLE' }")
    List<User> findAvailableWorkers(Pageable pageable);


    //@Query("SELECT w FROM User w WHERE w.role = WORKER AND " + HAVERSINE_FORMULA + " < :mindistance AND w.workSpecialization = :profession ORDER BY w.averageRating DESC")
    @Query("{ 'role' : 'WORKER', 'workSpecialization' : ?0 , 'availabilityStatus' : 'AVAILABLE' }")
    List<User> findAvailableWorkersByProfession(@Param("profession") String profession, Pageable pageable);

    @Query("{ 'role' : 'WORKER', 'workSpecialization' : ?0 , '$or': [{'availabilityStatus' : 'AVAILABLE'}, {'availabilityStatus' : 'MODERATED'}] }")
    List<User> findWorkersByProfession(@Param("profession") String profession, Pageable pageable);
}

