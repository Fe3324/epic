package com.app.epic.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn; // em segundos
    private UserResponseDTO user;
    private LocalDateTime authenticatedAt;
    
    public static AuthResponse of(String accessToken, String refreshToken, Long expiresIn, UserResponseDTO user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(user)
                .authenticatedAt(LocalDateTime.now())
                .build();
    }
} 