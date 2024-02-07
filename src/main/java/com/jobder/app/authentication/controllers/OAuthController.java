package com.jobder.app.authentication.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.jobder.app.authentication.config.JwtService;
import com.jobder.app.authentication.dto.JWTokenDTO;
import com.jobder.app.authentication.dto.RefreshDTO;
import com.jobder.app.authentication.dto.RegistrationDTO;
import com.jobder.app.authentication.dto.userdtos.LoginDTO;
import com.jobder.app.authentication.exceptions.InvalidAuthException;
import com.jobder.app.authentication.models.users.AvailabilityStatus;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Value("${cors.origin}")
    String corsOrigin;

    @Value("${google.clientId}")
    String googleClientId;

    @Value("${google.clientSecret}")
    String googleClientSecret;

    @Value("${secretPsw}")
    String secretPsw;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/check/code/google")
    public ResponseEntity<?> handleGoogleAuthCode(@RequestBody RegistrationDTO registrationDTO, HttpServletResponse authResponse) throws IOException {
        String code = registrationDTO.getValue();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(googleClientId, googleClientSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("redirect_uri", corsOrigin);
        params.put("grant_type", "authorization_code");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("https://www.googleapis.com/oauth2/v4/token", request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        String idToken = (String) responseBody.get("id_token");

        registrationDTO.setValue(idToken);

        JWTokenDTO loginInfo = this.google(registrationDTO);

        //setRefreshCookieToResponse(loginInfo.getRefreshToken(), authResponse);

        return new ResponseEntity<>(loginInfo,HttpStatus.OK);
    }

    private void setRefreshCookieToResponse(String refreshToken, HttpServletResponse authResponse){
        Cookie refreshCookie = new Cookie("refresh_token",refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setDomain("undefined");
        //refreshCookie.setAttribute("SameSite", "none");
        //refreshCookie.setPath("/oauth/refreshToken");
        refreshCookie.setMaxAge(3600 * 24 * 30);
        authResponse.setHeader("Access-Control-Allow-Credentials", "true");
        authResponse.setHeader("Access-Control-Allow-Headers", "X-Requested-With, WWW-Authenticate, Authorization, Origin, Content-Type, Version, Set-Cookie");
        authResponse.setHeader("Access-Control-Expose-Headers", "X-Requested-With, WWW-Authenticate, Authorization, Origin, Content-Type, Set-Cookie");

        authResponse.addCookie(refreshCookie);
    }

    private JWTokenDTO google(RegistrationDTO registrationDTO) throws IOException {
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

        return login(usuario);
    }

    private JWTokenDTO login(User usuario){
        //No necesito autenticar por que es logueo con google

        String jwt = jwtService.getToken(usuario);
        String refresh = jwtService.getRefresh(usuario);
        JWTokenDTO jwTokenDTO = new JWTokenDTO();
        jwTokenDTO.setRole(usuario.getRole().name());
        jwTokenDTO.setAccessToken(jwt);
        jwTokenDTO.setUserId(usuario.getId());
        jwTokenDTO.setRefreshToken(refresh);

        return jwTokenDTO;
    }


    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshDTO refreshDTO) {
        ResponseEntity<?> response;
        HttpStatus httpStatus = HttpStatus.OK;

        try{
            if(!jwtService.isRefreshTokenValid(refreshDTO.getRefreshToken())) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                throw new InvalidAuthException("Invalid Refresh token!");
            }

            if(jwtService.isTokenExpired(refreshDTO.getRefreshToken())) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                throw new InvalidAuthException("Expired Refresh token!");
            }

            String userEmail = jwtService.getUsernameFromToken(refreshDTO.getRefreshToken());
            User user = userService.findByEmail(userEmail).orElseThrow(()-> new InvalidAuthException("Invalid refresh Token!"));

            String jwtToken = jwtService.getToken(user);

            JWTokenDTO jwTokenDTO = new JWTokenDTO();
            jwTokenDTO.setAccessToken(jwtToken);
            jwTokenDTO.setUserId(user.getId());
            jwTokenDTO.setRole(user.getRole().name());

            response = new ResponseEntity<>(jwTokenDTO, null, httpStatus);
        }
        catch (InvalidAuthException e){
            response = new ResponseEntity<>(e.getMessage(), null , httpStatus);
        }

        return response;
    }


    @RequestMapping("/refreshTokenCookie")
    public ResponseEntity<?> refreshAccessTokenCookie(@CookieValue(name = "refresh_token") String refreshToken) {
        ResponseEntity<?> response;
        HttpStatus httpStatus = HttpStatus.OK;

        try{
            if(!jwtService.isRefreshTokenValid(refreshToken)) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                throw new InvalidAuthException("Invalid Refresh token!");
            }

            if(jwtService.isTokenExpired(refreshToken)) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                throw new InvalidAuthException("Expired Refresh token!");
            }

            String userEmail = jwtService.getUsernameFromToken(refreshToken);
            User user = userService.findByEmail(userEmail).orElseThrow(()-> new InvalidAuthException("Invalid refresh Token!"));

            String jwtToken = jwtService.getToken(user);

            JWTokenDTO jwTokenDTO = new JWTokenDTO();
            jwTokenDTO.setAccessToken(jwtToken);
            jwTokenDTO.setUserId(user.getId());
            jwTokenDTO.setRole(user.getRole().name());

            response = new ResponseEntity<>(jwTokenDTO, null, httpStatus);
        }
        catch (InvalidAuthException e){
            response = new ResponseEntity<>(e.getMessage(), null , httpStatus);
        }

        return response;
    }

    @PostMapping("/register/client/credentials")
    public JWTokenDTO registerWithCredentials(RegistrationDTO usuario, HttpServletResponse httpServletResponse) throws InvalidAuthException {
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getUsername(), usuario.getPassword()));
        if(userService.findByEmail(usuario.getEmail()).isPresent()){
            throw new InvalidAuthException("Email already exists!");
        }

        User savedUser = userService.registerClient(usuario);

        JWTokenDTO jwTokenDTO = login(savedUser);

        //setRefreshCookieToResponse(jwTokenDTO.getRefreshToken(), httpServletResponse);

        return jwTokenDTO;
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user){
        return null;
    }

    @PostMapping("/login/credentials")
    public ResponseEntity<?> loginWithCredentials(LoginDTO credentials, HttpServletResponse httpServletResponse) throws InvalidAuthException {
        ResponseEntity<?> response;
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));

            User savedUser = userService.findByEmail(credentials.getUsername()).orElseThrow(()->new InvalidAuthException("Invalid User!"));

            JWTokenDTO jwTokenDTO = login(savedUser);

            //setRefreshCookieToResponse(jwTokenDTO.getRefreshToken(), httpServletResponse);

            response = new ResponseEntity<>(jwTokenDTO, null, HttpStatus.OK);
        }
        catch (AuthenticationException | InvalidAuthException e){
            response = new ResponseEntity<>("Invalid credentials!", null, HttpStatus.BAD_REQUEST);
        }

        return response;
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