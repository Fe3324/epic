package com.app.epic.mapper;

import com.app.epic.dto.RoleCreateDTO;
import com.app.epic.dto.RoleResponseDTO;
import com.app.epic.dto.RoleUpdateDTO;
import com.app.epic.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    
    public RoleResponseDTO toResponseDTO(Role role) {
        if (role == null) {
            return null;
        }
        
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .build();
    }
    
    public Role toEntity(RoleCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        
        return Role.builder()
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .build();
    }
    
    public void updateEntityFromDTO(Role role, RoleUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return;
        }
        
        if (updateDTO.getName() != null) {
            role.setName(updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            role.setDescription(updateDTO.getDescription());
        }
    }
} 