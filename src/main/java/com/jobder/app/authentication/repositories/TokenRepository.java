package com.jobder.app.authentication.repositories;

import com.jobder.app.authentication.models.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
}
