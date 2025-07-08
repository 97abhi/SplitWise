package com.example.authService.Auth.Service.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authService.Auth.Service.entity.AuthToken;
import com.example.authService.Auth.Service.entity.AuthUserCredentials;
import com.example.authService.Auth.Service.repository.AuthTokenRepository;
import com.example.authService.Auth.Service.repository.AuthUserCredentialsRepository;
import com.example.authService.Auth.Service.service.AuthUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService{

    private final AuthUserCredentialsRepository authUserCredentialsRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthUserCredentials registerUser(Long userId, String plainPassword) {
        String hash = passwordEncoder.encode(plainPassword);
        AuthUserCredentials credentials = AuthUserCredentials.builder().userId(userId).passwordHash(hash).build();
        
        return authUserCredentialsRepository.save(credentials);
    }

    @Override
    public boolean validateCredentials(Long userId, String plainPassword) {
        return authUserCredentialsRepository.findByUserId(userId).map(creds -> passwordEncoder.matches(plainPassword,creds.getPasswordHash())).orElse(false);
    }

    @Override
    public List<AuthToken> getUserTokens(Long userId) {
       return authTokenRepository.findByUserId(userId);
    }

    @Override
    public void revokeTokens(Long userId) {
       List<AuthToken> tokens = authTokenRepository.findByUserId(userId);
       for(AuthToken token : tokens){
            token.setIsRevoked(true);
       }
       authTokenRepository.saveAll(tokens);
    }

}
