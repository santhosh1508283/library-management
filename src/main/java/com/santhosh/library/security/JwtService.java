package com.santhosh.library.security;

import com.santhosh.library.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(User user, long expiration){
        Date now = new Date();
        return Jwts.builder().subject(user.getEmail()).issuedAt(now).expiration(new Date(now.getTime() + expiration)).signWith(getSigningKey()).compact();
    }

    public String generateAccessToken(User user){
        return generateToken(user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user){
        return generateToken(user, refreshTokenExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user){
        Claims claims = extractAllClaims(token);
        return claims.getSubject().equals(user.getEmail()) && claims.getExpiration().after(new Date());
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    public boolean isRefreshTokenValid(String token, User user){
        return isTokenValid(token, user);
    }

}
