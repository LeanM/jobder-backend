package com.jobder.app.authentication.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.jobder.app.authentication.config.JwtService;
import com.jobder.app.authentication.dto.JWTokenDTO;
import com.jobder.app.authentication.dto.RegistrationDTO;
import com.jobder.app.authentication.exceptions.InvalidAuthException;
import com.jobder.app.authentication.models.AvailabilityStatus;
import com.jobder.app.authentication.models.User;
import com.jobder.app.authentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/oauth")
@RequiredArgsConstructor
public class OAuthController {

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Value("${google.clientId}")
    String googleClientId;

    @Value("${google.clientSecret}")
    String googleClientSecret;

    @Value("${secretPsw}")
    String secretPsw;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/check/code/google")
    public ResponseEntity<?> handleGoogleAuthCode(@RequestBody RegistrationDTO registrationDTO) throws IOException {
        String code = registrationDTO.getValue();
        String clientIdAndSecret = googleClientId + googleClientSecret;
        String authBasic = Base64.getEncoder().encodeToString(clientIdAndSecret.getBytes());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //headers.setBasicAuth(authBasic);
        headers.setBasicAuth(googleClientId, googleClientSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("redirect_uri", "http://localhost:3000");
        params.put("grant_type", "authorization_code");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("https://www.googleapis.com/oauth2/v4/token", request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        String idToken = (String) responseBody.get("id_token");

        registrationDTO.setValue(idToken);

        return this.google(registrationDTO);
    }

    private ResponseEntity google(RegistrationDTO registrationDTO) throws IOException {
        final NetHttpTransport netHttpTransport = new NetHttpTransport();
        final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier.Builder verifier =
                new GoogleIdTokenVerifier.Builder(netHttpTransport, jsonFactory)
                        .setAudience(Collections.singletonList(googleClientId));

        final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), registrationDTO.getValue());
        final GoogleIdToken.Payload payload = googleIdToken.getPayload();

        User usuario = new User();
        if(userService.existsEmail(payload.getEmail()))
            usuario = userService.getByEmail(payload.getEmail()).get();
        else {
            registrationDTO.setName((String) payload.getOrDefault("name",""));
            registrationDTO.setPicture((String) payload.getOrDefault("picture",""));
            registrationDTO.setEmail(payload.getEmail());
            usuario = saveUser(registrationDTO);
        }

        JWTokenDTO jwtokenRes = login(usuario);

        return new ResponseEntity(jwtokenRes, HttpStatus.OK);
    }

    private JWTokenDTO login(User usuario){
        //No necesito autenticar por que es logueo con google

        String jwt = jwtService.getToken(usuario);
        JWTokenDTO jwTokenDTO = new JWTokenDTO();
        jwTokenDTO.setRole(usuario.getRole().name());
        jwTokenDTO.setAccessToken(jwt);

        if(usuario.getRole().name().equals("CLIENT")){
            jwTokenDTO.setUserData(usuario.toClient());
        } else if (usuario.getRole().name().equals("WORKER")) {
            jwTokenDTO.setUserData(usuario.toWorker());
        }

        return jwTokenDTO;
    }

    @PostMapping("/register/client/credentials")
    public JWTokenDTO registerWithCredentials(RegistrationDTO usuario) throws InvalidAuthException {
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getUsername(), usuario.getPassword()));
        if(userService.findByEmail(usuario.getEmail()).isPresent()){
            throw new InvalidAuthException("Email already exists!");
        }

        User savedUser = userService.registerClient(usuario);

        String jwt = jwtService.getToken(savedUser);
        JWTokenDTO jwTokenDTO = new JWTokenDTO();
        jwTokenDTO.setAccessToken(jwt);

        return jwTokenDTO;
    }

    private User saveUser(RegistrationDTO registrationDTO){
        User usuario = new User();

        usuario.setEmail(registrationDTO.getEmail());
        usuario.setName(registrationDTO.getName());
        usuario.setPicture(registrationDTO.getPicture());
        usuario.setPassword(passwordEncoder.encode(secretPsw));
        usuario.setPhoneNumber(registrationDTO.getPhoneNumber());
        usuario.setAddress(registrationDTO.getAddress());
        usuario.setLatitude(registrationDTO.getLatitude());
        usuario.setLongitude(registrationDTO.getLongitude());
        usuario.setBirthDate(registrationDTO.getBirthDate());

        usuario.setRole(registrationDTO.getAccountRole());

        if(registrationDTO.getAccountRole().name().equals("WORKER")){
            usuario.setWorkSpecialization(registrationDTO.getWorkSpecialization());
            usuario.setAvailabilityStatus(AvailabilityStatus.MODERATED);
            usuario.setAverageRating(1f);
            usuario.setWorksFinished(0);
        } else if (registrationDTO.getAccountRole().name().equals("CLIENT")) {
            if(registrationDTO.getSearchParameters() != null){
                usuario.setSearchParameters(registrationDTO.getSearchParameters());
            }
        }

        return userService.save(usuario);
    }
}