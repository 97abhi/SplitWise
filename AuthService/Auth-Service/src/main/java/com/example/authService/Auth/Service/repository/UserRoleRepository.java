package com.example.authService.Auth.Service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authService.Auth.Service.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long>{

    List<UserRole> findByUserId(Long userId);
}
