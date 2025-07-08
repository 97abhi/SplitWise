package com.example.authService.Auth.Service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.authService.Auth.Service.entity.AuthUserCredentials;
import com.example.authService.Auth.Service.repository.AuthTokenRepository;
import com.example.authService.Auth.Service.repository.AuthUserCredentialsRepository;
import com.example.authService.Auth.Service.service.AuthTokenService;
import com.example.authService.Auth.Service.service.AuthUserService;
import com.example.authService.Auth.Service.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;
    private final JwtService jwtService;
    private final AuthTokenService authTokenService;
    //register
    @PostMapping("/register")
    public ResponseEntity<AuthUserCredentials> register(@RequestParam Long userId, @RequestParam String password){

        AuthUserCredentials authUserCredentials = authUserService.registerUser(userId, password);

        return ResponseEntity.ok(authUserCredentials);

    }

    //login
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestParam Long userId, String password){

        boolean validate = authUserService.validateCredentials(userId, password);
        if(!validate){
            return ResponseEntity.status(401).body(Map.of("Error", "Could not validate the credentials"));           
        }
        String token = jwtService.generateToken(userId);
        authTokenService.saveToken(userId, token); 
        return ResponseEntity.ok().body(Map.of("Token generated",token));
    }

    //logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(Long userId){
        authUserService.revokeTokens(userId);
        return ResponseEntity.ok().body("Logged out successfully and revoked tokens");
    }

    //validate tokens

    @PostMapping("/validate")
    public ResponseEntity<Map<String,Object>> validateTokens(String token){
        Long userId = jwtService.extractUserId(token);
        boolean valid  = jwtService.isTokenExpired(token) || jwtService.isTokenRevoked(token);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("Expired", valid);
        
        return ResponseEntity.ok(response);
    }




}
