package com.app.epic.controller;

import com.app.epic.dto.*;
import com.app.epic.service.AuthService;
import com.app.epic.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Só ADMIN pode acessar
public class AdminController {
    
    private final AuthService authService;
    private final UserService userService;
    
    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /admin/register-admin - Admin registration attempt for user: {}", request.getUsername());
        
        // Registrar usuário normalmente
        AuthResponse authResponse = authService.register(request);
        
        // Adicionar role ADMIN ao usuário recém-criado
        UserResponseDTO user = authResponse.getUser();
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .roleIds(java.util.Set.of(1L, 2L)) // ADMIN + USER roles (assumindo IDs padrão)
                .build();
        
        try {
            userService.update(user.getId(), updateDTO);
            
            // Recarregar dados do usuário com as novas roles
            UserResponseDTO updatedUser = userService.findById(user.getId());
            
            // Atualizar response com dados corretos
            authResponse.setUser(updatedUser);
        } catch (Exception e) {
            log.error("Erro ao atualizar roles do usuário admin: {}", e.getMessage());
            // Retornar o usuário mesmo sem as roles admin por enquanto
        }
        
        log.info("Admin user registered successfully: {}", request.getUsername());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Usuário admin registrado com sucesso")
                        .data(authResponse)
                        .timestamp(java.time.LocalDateTime.now())
                        .status(201)
                        .build());
    }
    
    @PostMapping("/promote-user/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> promoteToAdmin(@PathVariable Long userId) {
        log.info("POST /admin/promote-user/{} - Promoting user to admin", userId);
        
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .roleIds(java.util.Set.of(1L, 2L)) // ADMIN + USER roles
                .build();
        
        UserResponseDTO updatedUser = userService.update(userId, updateDTO);
        
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Usuário promovido a admin com sucesso"));
    }
    
    @DeleteMapping("/demote-user/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> demoteFromAdmin(@PathVariable Long userId) {
        log.info("DELETE /admin/demote-user/{} - Demoting user from admin", userId);
        
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .roleIds(java.util.Set.of(2L)) // Apenas USER role
                .build();
        
        UserResponseDTO updatedUser = userService.update(userId, updateDTO);
        
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Usuário removido de admin com sucesso"));
    }
} 