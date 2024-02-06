package com.jobder.app.authentication.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    String SECRET_KEY;

    public String getToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Permission","AUTH");
        return getToken(claims, user);
    }

    private String getToken(Map<String,Object> extraClaims, UserDetails user) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) //30 minutos
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getRefresh(UserDetails user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("Permission","REFRESH");
        return getRefreshToken(claims, user);
    }

    private String getRefreshToken(Map<String,Object> extraClaims, UserDetails user) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24))) //1 dia
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token){
        try{
            String permission = (String) Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody().get("Permission");
            return !permission.equals("REFRESH");
        }
        catch (Exception ex){
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token){
        try{
            String permission = (String) Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody().get("Permission");
            return permission.equals("REFRESH");
        }
        catch (Exception ex){
            return false;
        }
    }

    private Claims getAllClaims(String token)
    {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims=getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token)
    {
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token)
    {
        return getExpiration(token).before(new Date());
    }
}
