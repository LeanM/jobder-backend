package com.jobder.app.authentication.repositories;

import com.jobder.app.authentication.models.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    Optional<Token> findByUserId(String userId);
    Optional<Token> findByToken(String token);
}
