package com.jobder.app.authentication.services;

import com.jobder.app.authentication.config.JwtService;
import com.jobder.app.authentication.exceptions.InvalidAuthException;
import com.jobder.app.authentication.models.Token;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    public Token refreshTokenToUser(User user) throws InvalidAuthException {
        Token existingToken = tokenRepository.findByUserId(user.getId()).orElse(null);
        if(existingToken != null){
            if(!existingToken.getIsRevoked()){
                String accessToken = jwtService.getToken(user);
                existingToken.setToken(accessToken);
                return tokenRepository.save(existingToken);
            }
            else throw new InvalidAuthException("Old Refresh token!");
        } throw new InvalidAuthException("Old Refresh token!");
    }
    public Token createTokenToUser(User user){
        String accessToken = jwtService.getToken(user);

        Token token = new Token();
        token.setToken(accessToken);
        token.setUserId(user.getId());
        token.setIsRevoked(false);

        Token existingToken = tokenRepository.findByUserId(user.getId()).orElse(null);
        if(existingToken != null){
            existingToken.setToken(accessToken);
            return tokenRepository.save(existingToken);
        }
        else return tokenRepository.save(token);
    }

    public void cleanUserToken(User user){
        Token token = tokenRepository.findByUserId(user.getId()).orElse(null);
        if(token != null)
            tokenRepository.delete(token);
    }

    public String getRefreshTokenOfUser(User user){
        return jwtService.getRefresh(user);
    }
}
