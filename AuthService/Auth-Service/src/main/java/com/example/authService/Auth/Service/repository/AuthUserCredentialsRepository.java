package com.example.authService.Auth.Service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authService.Auth.Service.entity.AuthUserCredentials;

public interface AuthUserCredentialsRepository extends JpaRepository<AuthUserCredentials, Long>{

    Optional<AuthUserCredentials> findByUserId(Long user_id);

}
