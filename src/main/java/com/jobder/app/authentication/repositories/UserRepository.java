package com.jobder.app.authentication.repositories;

import com.jobder.app.authentication.models.User;
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

    @Query("SELECT w FROM User w WHERE w.role = WORKER AND " + HAVERSINE_FORMULA + " < :mindistance ORDER BY w.averageRating DESC")
    List<User> findCloseWorkers(@Param("longitude") Double clientLongitude, @Param("latitude") Double clientLatitude, @Param("mindistance") int minimumDistanceInKm, Pageable pageable);


    @Query("SELECT w FROM User w WHERE w.role = WORKER AND " + HAVERSINE_FORMULA + " < :mindistance AND w.workSpecialization = :profession ORDER BY w.averageRating DESC")
    List<User> findCloseWorkersByProfession(@Param("profession") String profession, @Param("longitude") Double clientLongitude, @Param("latitude") Double clientLatitude, @Param("mindistance") int minimumDistanceInKm, Pageable pageable);
}

