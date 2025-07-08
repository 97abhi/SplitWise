package com.example.authService.Auth.Service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authService.Auth.Service.entity.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken,Long>{
    List<AuthToken> findByUserId(Long userId);
    Optional<AuthToken> findByToken(String token);
    

}
