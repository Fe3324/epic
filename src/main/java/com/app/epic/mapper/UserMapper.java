package com.app.epic.mapper;

import com.app.epic.dto.*;
import com.app.epic.entity.User;
import com.app.epic.entity.Role;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    private final RoleMapper roleMapper;
    private final AddressMapper addressMapper;
    
    public UserMapper(RoleMapper roleMapper, AddressMapper addressMapper) {
        this.roleMapper = roleMapper;
        this.addressMapper = addressMapper;
    }
    
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .lastLogin(user.getLastLogin())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(user.getRoles() != null ? 
                    user.getRoles().stream()
                            .map(roleMapper::toResponseDTO)
                            .collect(Collectors.toSet()) : 
                    Collections.emptySet())
                .addresses(user.getAddresses() != null ? 
                    user.getAddresses().stream()
                            .map(addressMapper::toResponseDTO)
                            .collect(Collectors.toSet()) : 
                    Collections.emptySet())
                .build();
    }
    
    public User toEntity(UserCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        
        return User.builder()
                .username(createDTO.getUsername())
                .email(createDTO.getEmail())
                .password(createDTO.getPassword()) // Será criptografada no service
                .firstName(createDTO.getFirstName())
                .lastName(createDTO.getLastName())
                .phone(createDTO.getPhone())
                .birthDate(createDTO.getBirthDate())
                .enabled(createDTO.getEnabled() != null ? createDTO.getEnabled() : true)
                .build();
    }
    
    public void updateEntityFromDTO(User user, UserUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return;
        }
        
        if (updateDTO.getUsername() != null) {
            user.setUsername(updateDTO.getUsername());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPassword() != null) {
            user.setPassword(updateDTO.getPassword()); // Será criptografada no service
        }
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getBirthDate() != null) {
            user.setBirthDate(updateDTO.getBirthDate());
        }
        if (updateDTO.getEnabled() != null) {
            user.setEnabled(updateDTO.getEnabled());
        }
    }
} 