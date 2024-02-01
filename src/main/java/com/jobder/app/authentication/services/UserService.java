package com.jobder.app.authentication.services;

import com.jobder.app.authentication.dto.RegistrationDTO;
import com.jobder.app.authentication.models.RoleName;
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

    public User registerClient(RegistrationDTO registrationDTO){
        User newUser = new User();
        newUser.setName(registrationDTO.getName());
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setPassword(registrationDTO.getPassword());
        newUser.setLatitude(registrationDTO.getLatitude());
        newUser.setLongitude(registrationDTO.getLongitude());
        newUser.setRole(RoleName.CLIENT);

        return userRepository.save(newUser);
    }

    public User registerWorker(RegistrationDTO registrationDTO){
        User newUser = new User();
        newUser.setName(registrationDTO.getName());
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setPassword(registrationDTO.getPassword());
        newUser.setLatitude(registrationDTO.getLatitude());
        newUser.setLongitude(registrationDTO.getLongitude());
        newUser.setRole(RoleName.WORKER);

        return userRepository.save(newUser);
    }

    public User save(User usuario){
        return userRepository.save(usuario);
    }
}
