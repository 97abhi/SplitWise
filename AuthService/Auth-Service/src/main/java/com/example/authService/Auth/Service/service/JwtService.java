package com.example.authService.Auth.Service.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.authService.Auth.Service.entity.AuthToken;
import com.example.authService.Auth.Service.repository.AuthTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;  // must be at least 256 bits (32+ ASCII chars)

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Autowired
    AuthTokenRepository authTokenRepository;


    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);  // ✅ ensures secure HS256-compatible key
    }

    // Generate JWT
    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token and return userId
    public Long validateTokenAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(getSigningKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid or expired JWT token");
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                                  .setSigningKey(getSigningKey())
                                  .build()
                                  .parseClaimsJws(token)
                                  .getBody()
                                  .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public boolean isTokenInvalid(String token) {
    return isTokenExpired(token) || isTokenRevoked(token);
}

public boolean isTokenRevoked(String token) {
    Optional<AuthToken> storedToken = authTokenRepository.findByToken(token);
    return storedToken.map(AuthToken::getIsRevoked).orElse(true); // true if not found
}


    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
