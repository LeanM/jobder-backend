package com.jobder.app.authentication.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.jobder.app.authentication.config.JwtService;
import com.jobder.app.authentication.dto.JWTokenDTO;
import com.jobder.app.authentication.dto.TokenDTO;
import com.jobder.app.authentication.models.RoleName;
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
@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> handleGoogleAuthCode(@RequestBody TokenDTO tokenDTO) throws IOException {
        String code = tokenDTO.getValue();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", googleClientId);
        params.put("client_secret", googleClientSecret);
        params.put("redirect_uri", "http://localhost:3000");
        params.put("grant_type", "authorization_code");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        String idToken = (String) responseBody.get("id_token");

        tokenDTO.setValue(idToken);

        return this.google(tokenDTO);
    }

    private ResponseEntity google(TokenDTO tokenDTO) throws IOException {
        final NetHttpTransport netHttpTransport = new NetHttpTransport();
        final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier.Builder verifier =
                new GoogleIdTokenVerifier.Builder(netHttpTransport, jsonFactory)
                        .setAudience(Collections.singletonList(googleClientId));

        final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), tokenDTO.getValue());
        final GoogleIdToken.Payload payload = googleIdToken.getPayload();

        System.out.println(payload);

        User usuario = new User();
        if(userService.existsEmail(payload.getEmail()))
            usuario = userService.getByEmail(payload.getEmail()).get();
        else
            usuario = saveUser(payload.getEmail());

        JWTokenDTO jwtokenRes = login(usuario);

        return new ResponseEntity(jwtokenRes, HttpStatus.OK);
    }

    private JWTokenDTO login(User usuario){
        //No necesito autenticar por que es logueo con google

        String jwt = jwtService.getToken(usuario);
        JWTokenDTO jwTokenDTO = new JWTokenDTO();
        jwTokenDTO.setAccessToken(jwt);

        return jwTokenDTO;
    }

    private JWTokenDTO loginWithCredentials(User usuario){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getUsername(), usuario.getPassword()));
        UserDetails user = userService.findByEmail(usuario.getEmail()).orElseThrow();

        String jwt = jwtService.getToken(user);
        JWTokenDTO jwTokenDTO = new JWTokenDTO();
        jwTokenDTO.setAccessToken(jwt);

        return jwTokenDTO;
    }

    private User saveUser(String email){
        User usuario = new User();

        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(secretPsw));
        usuario.setRole(RoleName.CLIENT);

        return userService.save(usuario);
    }
}