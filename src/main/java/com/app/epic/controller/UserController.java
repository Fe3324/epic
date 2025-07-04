package com.app.epic.controller;

import com.app.epic.dto.*;
import com.app.epic.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDTO>>> findAll(
            @PageableDefault(size = 20, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("GET /api/users - Finding all users with pagination");
        
        PageResponse<UserResponseDTO> users = userService.findAll(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(users, "Usuários listados com sucesso"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> findById(@PathVariable Long id) {
        log.info("GET /api/users/{} - Finding user by id", id);
        
        UserResponseDTO user = userService.findById(id);
        
        return ResponseEntity.ok(ApiResponse.success(user, "Usuário encontrado com sucesso"));
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> findByUsername(@PathVariable String username) {
        log.info("GET /api/users/username/{} - Finding user by username", username);
        
        UserResponseDTO user = userService.findByUsername(username);
        
        return ResponseEntity.ok(ApiResponse.success(user, "Usuário encontrado com sucesso"));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> findByEmail(@PathVariable String email) {
        log.info("GET /api/users/email/{} - Finding user by email", email);
        
        UserResponseDTO user = userService.findByEmail(email);
        
        return ResponseEntity.ok(ApiResponse.success(user, "Usuário encontrado com sucesso"));
    }
    
    @GetMapping("/role/{roleName}")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> findByRoleName(@PathVariable String roleName) {
        log.info("GET /api/users/role/{} - Finding users by role name", roleName);
        
        List<UserResponseDTO> users = userService.findByRoleName(roleName);
        
        return ResponseEntity.ok(ApiResponse.success(users, "Usuários com role '" + roleName + "' listados com sucesso"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> searchByName(@RequestParam String name) {
        log.info("GET /api/users/search?name={} - Searching users by name", name);
        
        List<UserResponseDTO> users = userService.searchByName(name);
        
        return ResponseEntity.ok(ApiResponse.success(users, "Busca realizada com sucesso"));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(@Valid @RequestBody UserCreateDTO createDTO) {
        log.info("POST /api/users - Creating new user with username: {}", createDTO.getUsername());
        
        UserResponseDTO createdUser = userService.create(createDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdUser));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(
            @PathVariable Long id, 
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        
        log.info("PUT /api/users/{} - Updating user", id);
        
        UserResponseDTO updatedUser = userService.update(id, updateDTO);
        
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Usuário atualizado com sucesso"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        
        userService.delete(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Usuário deletado com sucesso"));
    }
    
    @PatchMapping("/{id}/last-login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateLastLogin(@PathVariable Long id) {
        log.info("PATCH /api/users/{}/last-login - Updating last login", id);
        
        UserResponseDTO updatedUser = userService.updateLastLogin(id);
        
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Último login atualizado com sucesso"));
    }
} 