package com.app.epic.controller;

import com.app.epic.dto.*;
import com.app.epic.service.RoleService;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoleResponseDTO>>> findAll(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("GET /api/roles - Finding all roles with pagination");
        
        PageResponse<RoleResponseDTO> roles = roleService.findAll(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(roles, "Roles listadas com sucesso"));
    }
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> findAllOrderByName() {
        log.info("GET /api/roles/all - Finding all roles ordered by name");
        
        List<RoleResponseDTO> roles = roleService.findAllOrderByName();
        
        return ResponseEntity.ok(ApiResponse.success(roles, "Todas as roles listadas com sucesso"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> findById(@PathVariable Long id) {
        log.info("GET /api/roles/{} - Finding role by id", id);
        
        RoleResponseDTO role = roleService.findById(id);
        
        return ResponseEntity.ok(ApiResponse.success(role, "Role encontrada com sucesso"));
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> findByName(@PathVariable String name) {
        log.info("GET /api/roles/name/{} - Finding role by name", name);
        
        RoleResponseDTO role = roleService.findByName(name);
        
        return ResponseEntity.ok(ApiResponse.success(role, "Role encontrada com sucesso"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> searchByName(@RequestParam String name) {
        log.info("GET /api/roles/search?name={} - Searching roles by name", name);
        
        List<RoleResponseDTO> roles = roleService.searchByName(name);
        
        return ResponseEntity.ok(ApiResponse.success(roles, "Busca realizada com sucesso"));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponseDTO>> create(@Valid @RequestBody RoleCreateDTO createDTO) {
        log.info("POST /api/roles - Creating new role with name: {}", createDTO.getName());
        
        RoleResponseDTO createdRole = roleService.create(createDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdRole));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> update(
            @PathVariable Long id, 
            @Valid @RequestBody RoleUpdateDTO updateDTO) {
        
        log.info("PUT /api/roles/{} - Updating role", id);
        
        RoleResponseDTO updatedRole = roleService.update(id, updateDTO);
        
        return ResponseEntity.ok(ApiResponse.success(updatedRole, "Role atualizada com sucesso"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("DELETE /api/roles/{} - Deleting role", id);
        
        roleService.delete(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Role deletada com sucesso"));
    }
} 