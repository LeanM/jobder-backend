package com.jobder.app.authentication.services;

import com.jobder.app.authentication.config.JwtService;
import com.jobder.app.authentication.dto.ChangePasswordDTO;
import com.jobder.app.authentication.dto.JWTokenDTO;
import com.jobder.app.authentication.dto.RefreshDTO;
import com.jobder.app.authentication.dto.RegistrationDTO;
import com.jobder.app.authentication.dto.userdtos.ClientDTO;
import com.jobder.app.authentication.dto.userdtos.WorkerDTO;
import com.jobder.app.authentication.exceptions.InvalidAuthException;
import com.jobder.app.authentication.exceptions.InvalidClientException;
import com.jobder.app.authentication.exceptions.InvalidWorkerException;
import com.jobder.app.authentication.models.Token;
import com.jobder.app.authentication.models.users.AvailabilityStatus;
import com.jobder.app.authentication.models.users.RoleName;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.TokenRepository;
import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.review.exceptions.ReviewException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final RandomPasswordGenerator randomPasswordGenerator;
    private final AuthenticationManager authenticationManager;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User getClientById(String clientId) throws InvalidClientException {
        User client = userRepository.findById(clientId).orElseThrow(()-> new InvalidClientException("Client doesnt exists!"));
        if(!client.getRole().equals(RoleName.CLIENT))
            throw new InvalidClientException("User is not a client!");

        return client;
    }

    public User getWorkerById(String workerId) throws InvalidWorkerException {
        User worker = userRepository.findById(workerId).orElseThrow(()-> new InvalidWorkerException("Worker doesnt exists!"));
        if(!worker.getRole().equals(RoleName.WORKER))
            throw new InvalidWorkerException("User is not a worker!");

        return worker;
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
            usuario.setIsGoogleUser(false);
            usuario.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
            usuario.setPhoneNumber(registrationDTO.getPhoneNumber());
            usuario.setAddress(registrationDTO.getAddress());
            usuario.setLatitude(registrationDTO.getLatitude());
            usuario.setLongitude(registrationDTO.getLongitude());
            usuario.setBirthDate(registrationDTO.getBirthDate());

            if (registrationDTO.getAccountRole().name().equals("WORKER")) {
                usuario.setWorkSpecialization(registrationDTO.getWorkSpecialization());
                usuario.setAvailabilityStatus(AvailabilityStatus.MODERATED);
                usuario.setAverageRating("1");
                usuario.setWorksFinished(0);
                usuario.setTotalReviews(0);
            } else if (registrationDTO.getAccountRole().name().equals("CLIENT")) {
                if (registrationDTO.getSearchParameters() != null) {
                    usuario.setSearchParameters(registrationDTO.getSearchParameters());
                }
            }
        } else {
            usuario.setIsGoogleUser(true);
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

    public void updateClient(String clientId, ClientDTO clientDTO) throws InvalidClientException{
        User clientToUpdate = userRepository.findById(clientId).orElseThrow(()->new InvalidClientException("Client doesnt exists!"));

        if(clientDTO.getAddress() != null) clientToUpdate.setAddress(clientDTO.getAddress());
        if(clientDTO.getName() != null) clientToUpdate.setName(clientDTO.getName());
        if(clientDTO.getBirthDate() != null) clientToUpdate.setBirthDate(clientDTO.getBirthDate());
        if(clientDTO.getLatitude() != null) clientToUpdate.setLatitude(clientDTO.getLatitude());
        if(clientDTO.getLongitude() != null) clientToUpdate.setLongitude(clientDTO.getLongitude());
        if(clientDTO.getPhoneNumber() != null) clientToUpdate.setPhoneNumber(clientDTO.getPhoneNumber());

        userRepository.save(clientToUpdate);
    }

    public void updateWorker(String workerId, WorkerDTO workerDTO) throws InvalidWorkerException {
        User workerToUpdate = userRepository.findById(workerId).orElseThrow(()->new InvalidWorkerException("Client doesnt exists!"));

        if(workerDTO.getAddress() != null) workerToUpdate.setAddress(workerDTO.getAddress());
        if(workerDTO.getName() != null) workerToUpdate.setName(workerDTO.getName());
        if(workerDTO.getBirthDate() != null) workerToUpdate.setBirthDate(workerDTO.getBirthDate());
        if(workerDTO.getLatitude() != null) workerToUpdate.setLatitude(workerDTO.getLatitude());
        if(workerDTO.getLongitude() != null) workerToUpdate.setLongitude(workerDTO.getLongitude());
        if(workerDTO.getPhoneNumber() != null) workerToUpdate.setPhoneNumber(workerDTO.getPhoneNumber());

        if(workerDTO.getWorkSpecialization() != null) workerToUpdate.setWorkSpecialization(workerDTO.getWorkSpecialization());
        if(workerDTO.getAvailabilityStatus() != null) workerToUpdate.setAvailabilityStatus(workerDTO.getAvailabilityStatus());
        if(workerDTO.getDescription() != null) workerToUpdate.setDescription(workerDTO.getDescription());
        if(workerDTO.getWorkingHours() != null) workerToUpdate.setWorkingHours(workerDTO.getWorkingHours());

        userRepository.save(workerToUpdate);
    }

    public void updatePassword(User user, ChangePasswordDTO changePasswordDTO) throws InvalidAuthException {
        if(user.getIsGoogleUser() != null && user.getIsGoogleUser()){
            user.setIsGoogleUser(false);
            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userRepository.save(user);
        } else {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), changePasswordDTO.getPreviousPassword()));
                user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
                userRepository.save(user);
            }catch (Exception e) {
                throw new InvalidAuthException("Previous password is incorrect!");
            }
        }
    }

    public void addWorkerReview(String workerId, Float ratingOfNewReview) throws InvalidWorkerException {
        User worker = userRepository.findById(workerId).orElseThrow(() -> new InvalidWorkerException("Worker doesnt exists!"));

        Float averageRating = Float.parseFloat(worker.getAverageRating());
        int totalReviewsQuantity = worker.getTotalReviews();
        Float totalRating = averageRating * totalReviewsQuantity;

        totalRating += ratingOfNewReview;
        totalReviewsQuantity += 1;

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        String newAverageRating = df.format(totalRating / totalReviewsQuantity);

        worker.setTotalReviews(totalReviewsQuantity);
        worker.setAverageRating(newAverageRating);

        userRepository.save(worker);
    }

    public void addFinishedWorkToWorker(String workerId) throws InvalidWorkerException {
        User worker = userRepository.findById(workerId).orElseThrow(()->new InvalidWorkerException("Worker doesn't exists!"));

        worker.setWorksFinished(worker.getWorksFinished() + 1);
    }
}
