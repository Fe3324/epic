package com.app.epic.service;

import com.app.epic.dto.*;
import com.app.epic.entity.Role;
import com.app.epic.entity.User;
import com.app.epic.exception.BusinessException;
import com.app.epic.exception.ResourceNotFoundException;
import com.app.epic.mapper.UserMapper;
import com.app.epic.repository.RoleRepository;
import com.app.epic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public PageResponse<UserResponseDTO> findAll(Pageable pageable) {
        log.debug("Finding all users with pagination: {}", pageable);
        
        Page<User> userPage = userRepository.findAll(pageable);
        
        List<UserResponseDTO> content = userPage.getContent()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(content, userPage.getNumber(), userPage.getSize(), userPage.getTotalElements());
    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        log.debug("Finding user by id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        return userMapper.toResponseDTO(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "username", username));
        
        return userMapper.toResponseDTO(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));
        
        return userMapper.toResponseDTO(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByRoleName(String roleName) {
        log.debug("Finding users by role name: {}", roleName);
        
        List<User> users = userRepository.findByRoleName(roleName);
        
        return users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchByName(String name) {
        log.debug("Searching users by name containing: {}", name);
        
        List<User> users = userRepository.findByNameContaining(name);
        
        return users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    public UserResponseDTO create(UserCreateDTO createDTO) {
        log.debug("Creating new user with username: {}", createDTO.getUsername());
        
        // Validar se username já existe
        if (userRepository.existsByUsername(createDTO.getUsername())) {
            throw new BusinessException("Username já está em uso: " + createDTO.getUsername());
        }
        
        // Validar se email já existe
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new BusinessException("Email já está em uso: " + createDTO.getEmail());
        }
        
        // Converter DTO para entidade
        User user = userMapper.toEntity(createDTO);
        
        // Criptografar senha
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        
        // Adicionar roles se especificadas
        if (createDTO.getRoleIds() != null && !createDTO.getRoleIds().isEmpty()) {
            Set<Role> roles = findRolesByIds(createDTO.getRoleIds());
            roles.forEach(user::addRole);
        }
        
        // Salvar usuário
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with id: {}", savedUser.getId());
        
        return userMapper.toResponseDTO(savedUser);
    }
    
    public UserResponseDTO update(Long id, UserUpdateDTO updateDTO) {
        log.debug("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        // Validar username único (se está sendo alterado)
        if (updateDTO.getUsername() != null && !updateDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateDTO.getUsername())) {
                throw new BusinessException("Username já está em uso: " + updateDTO.getUsername());
            }
        }
        
        // Validar email único (se está sendo alterado)
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new BusinessException("Email já está em uso: " + updateDTO.getEmail());
            }
        }
        
        // Atualizar dados do usuário
        userMapper.updateEntityFromDTO(user, updateDTO);
        
        // Criptografar nova senha se fornecida
        if (updateDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        // Atualizar roles se especificadas
        if (updateDTO.getRoleIds() != null) {
            user.getRoles().clear();
            if (!updateDTO.getRoleIds().isEmpty()) {
                Set<Role> roles = findRolesByIds(updateDTO.getRoleIds());
                roles.forEach(user::addRole);
            }
        }
        
        User savedUser = userRepository.save(user);
        
        log.info("User updated successfully with id: {}", savedUser.getId());
        
        return userMapper.toResponseDTO(savedUser);
    }
    
    public void delete(Long id) {
        log.debug("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário", "id", id);
        }
        
        userRepository.deleteById(id);
        
        log.info("User deleted successfully with id: {}", id);
    }
    
    public UserResponseDTO updateLastLogin(Long id) {
        log.debug("Updating last login for user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        user.setLastLogin(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        return userMapper.toResponseDTO(savedUser);
    }
    
    private Set<Role> findRolesByIds(Set<Long> roleIds) {
        Set<Role> roles = new HashSet<>();
        
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
            roles.add(role);
        }
        
        return roles;
    }
} 