package com.example.authService.Auth.Service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authService.Auth.Service.entity.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(String role);
}
