package com.example.authService.Auth.Service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auth_tokens")
@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class AuthToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 512)
    private String token;  

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "is_revoked")
    private Boolean isRevoked;

    @PrePersist
    protected void onCreate(){
        this.issuedAt = LocalDateTime.now();
        if(this.isRevoked == null){
            isRevoked = false;
        }
    }



}
