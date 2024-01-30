package com.jobder.app.authentication.services;

import com.jobder.app.authentication.models.User;
import com.jobder.app.authentication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public boolean existsEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public User save(User usuario){
        return userRepository.save(usuario);
    }
}
