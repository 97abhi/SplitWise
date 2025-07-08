package com.example.authService.Auth.Service.service;

import java.util.List;

import com.example.authService.Auth.Service.entity.AuthToken;
import com.example.authService.Auth.Service.entity.AuthUserCredentials;

public interface AuthUserService {

    AuthUserCredentials registerUser(Long userId, String plainPassword);

    boolean validateCredentials(Long userId, String plainPassword);

    List<AuthToken> getUserTokens(Long userId);

    void revokeTokens(Long userId);

}
