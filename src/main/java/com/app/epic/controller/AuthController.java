package com.app.epic.controller;

import com.app.epic.dto.*;
import com.app.epic.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /auth/login - Login attempt for user: {}", request.getUsername());
        
        AuthResponse authResponse = authService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login realizado com sucesso"));
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/register - Registration attempt for user: {}", request.getUsername());
        
        AuthResponse authResponse = authService.register(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Usuário registrado com sucesso")
                        .data(authResponse)
                        .timestamp(java.time.LocalDateTime.now())
                        .status(201)
                        .build());
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /auth/refresh - Token refresh attempt");
        
        AuthResponse authResponse = authService.refreshToken(request);
        
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Token renovado com sucesso"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
        log.info("POST /auth/logout - Logout attempt");
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        authService.logout(token);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Logout realizado com sucesso"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getCurrentUser() {
        log.info("GET /auth/me - Get current user");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserResponseDTO user = authService.getCurrentUser(username);
        
        return ResponseEntity.ok(ApiResponse.success(user, "Usuário atual recuperado com sucesso"));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        log.info("GET /auth/validate - Token validation");
        
        boolean isValid = false;
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            isValid = authentication != null && authentication.isAuthenticated();
        }
        
        return ResponseEntity.ok(ApiResponse.success(isValid, 
            isValid ? "Token válido" : "Token inválido"));
    }
    
    // ENDPOINT TEMPORÁRIO PARA DESENVOLVIMENTO - REGISTRAR ADMIN INICIAL
    @PostMapping("/register-first-admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerFirstAdmin(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/register-first-admin - First admin registration attempt for user: {}", request.getUsername());
        
        AuthResponse authResponse = authService.registerWithRoles(request, 
            java.util.Set.of("ADMIN", "USER"));
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Primeiro usuário admin registrado com sucesso")
                        .data(authResponse)
                        .timestamp(java.time.LocalDateTime.now())
                        .status(201)
                        .build());
    }
} 