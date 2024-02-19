package com.jobder.app.authentication.services;

import com.jobder.app.authentication.config.JwtService;
import com.jobder.app.authentication.dto.JWTokenDTO;
import com.jobder.app.authentication.dto.RefreshDTO;
import com.jobder.app.authentication.dto.RegistrationDTO;
import com.jobder.app.authentication.exceptions.InvalidAuthException;
import com.jobder.app.authentication.exceptions.InvalidClientException;
import com.jobder.app.authentication.models.Token;
import com.jobder.app.authentication.models.users.AvailabilityStatus;
import com.jobder.app.authentication.models.users.RoleName;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.TokenRepository;
import com.jobder.app.authentication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final RandomPasswordGenerator randomPasswordGenerator;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public boolean existsEmail(String email){
        return userRepository.existsByEmail(email);
    }


    public void resetUserSearchParameters(User user) throws InvalidClientException {
        if(user.getRole().name().equals("CLIENT")){
            user.setSearchParameters(null);
            userRepository.save(user);
        }
        else throw new InvalidClientException("User is not a client!");
    }

    public User save(User usuario){
        return userRepository.save(usuario);
    }

    public JWTokenDTO login(User usuario){
        //No necesito autenticar por que es logueo con google

        String refresh = tokenService.getRefreshTokenOfUser(usuario);
        Token accessToken = tokenService.createTokenToUser(usuario);

        JWTokenDTO jwTokenDTO = new JWTokenDTO();
        jwTokenDTO.setRole(usuario.getRole().name());
        jwTokenDTO.setAccessToken(accessToken.getToken());
        jwTokenDTO.setUserId(usuario.getId());
        jwTokenDTO.setRefreshToken(refresh);

        return jwTokenDTO;
    }

    public User registerUser(RegistrationDTO registrationDTO) {
        User usuario = new User();

        usuario.setEmail(registrationDTO.getEmail());
        usuario.setName(registrationDTO.getName());
        usuario.setPicture(registrationDTO.getPicture());
        usuario.setRole(registrationDTO.getAccountRole());

        if(!registrationDTO.getIsGoogleRegister()) {
            usuario.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
            usuario.setPhoneNumber(registrationDTO.getPhoneNumber());
            usuario.setAddress(registrationDTO.getAddress());
            usuario.setLatitude(registrationDTO.getLatitude());
            usuario.setLongitude(registrationDTO.getLongitude());
            usuario.setBirthDate(registrationDTO.getBirthDate());

            if (registrationDTO.getAccountRole().name().equals("WORKER")) {
                usuario.setWorkSpecialization(registrationDTO.getWorkSpecialization());
                usuario.setAvailabilityStatus(AvailabilityStatus.MODERATED);
                usuario.setAverageRating(1f);
                usuario.setWorksFinished(0);
            } else if (registrationDTO.getAccountRole().name().equals("CLIENT")) {
                if (registrationDTO.getSearchParameters() != null) {
                    usuario.setSearchParameters(registrationDTO.getSearchParameters());
                }
            }
        } else {
            usuario.setPassword(passwordEncoder.encode(randomPasswordGenerator.getRandomPassword()));
        }

        return save(usuario);
    }

    public JWTokenDTO refreshUserToken(RefreshDTO refreshDTO) throws InvalidAuthException {
        if(!jwtService.isRefreshTokenValid(refreshDTO.getRefreshToken())) {
            throw new InvalidAuthException("Invalid Refresh token!");
        }

        if(jwtService.isTokenExpired(refreshDTO.getRefreshToken())) {
            throw new InvalidAuthException("Expired Refresh token!");
        }

        String userEmail = jwtService.getUsernameFromToken(refreshDTO.getRefreshToken());
        User user = findByEmail(userEmail).orElseThrow(()-> new InvalidAuthException("Invalid refresh Token!"));

        Token accessToken = tokenService.refreshTokenToUser(user);

        JWTokenDTO jwTokenDTO = new JWTokenDTO();
        jwTokenDTO.setAccessToken(accessToken.getToken());
        jwTokenDTO.setUserId(user.getId());
        jwTokenDTO.setRole(user.getRole().name());

        return jwTokenDTO;
    }
}
