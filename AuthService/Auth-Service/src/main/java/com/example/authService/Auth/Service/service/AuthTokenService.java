package com.example.authService.Auth.Service.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.authService.Auth.Service.entity.AuthToken;
import com.example.authService.Auth.Service.repository.AuthTokenRepository;

@Service
public class AuthTokenService {

    @Autowired
    AuthTokenRepository authTokenRepository;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public void saveToken(Long userId, String token) {
    Duration duration = Duration.ofMillis(jwtExpirationMs);
    AuthToken authToken = new AuthToken();
    authToken.setUserId(userId);
    authToken.setToken(token);
    authToken.setTokenType("Bearer");
    authToken.setIssuedAt(LocalDateTime.now());
    authToken.setExpiryTime(LocalDateTime.now().plus(duration));
    authToken.setIsRevoked(false);
    authTokenRepository.save(authToken);
}
}
