package com.app.epic.service;

import com.app.epic.dto.*;
import com.app.epic.entity.Role;
import com.app.epic.exception.BusinessException;
import com.app.epic.exception.ResourceNotFoundException;
import com.app.epic.mapper.RoleMapper;
import com.app.epic.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    
    @Transactional(readOnly = true)
    public PageResponse<RoleResponseDTO> findAll(Pageable pageable) {
        log.debug("Finding all roles with pagination: {}", pageable);
        
        Page<Role> rolePage = roleRepository.findAll(pageable);
        
        List<RoleResponseDTO> content = rolePage.getContent()
                .stream()
                .map(roleMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(content, rolePage.getNumber(), rolePage.getSize(), rolePage.getTotalElements());
    }
    
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> findAllOrderByName() {
        log.debug("Finding all roles ordered by name");
        
        List<Role> roles = roleRepository.findAllOrderByName();
        
        return roles.stream()
                .map(roleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public RoleResponseDTO findById(Long id) {
        log.debug("Finding role by id: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        
        return roleMapper.toResponseDTO(role);
    }
    
    @Transactional(readOnly = true)
    public RoleResponseDTO findByName(String name) {
        log.debug("Finding role by name: {}", name);
        
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
        
        return roleMapper.toResponseDTO(role);
    }
    
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> searchByName(String name) {
        log.debug("Searching roles by name containing: {}", name);
        
        List<Role> roles = roleRepository.findByNameContaining(name);
        
        return roles.stream()
                .map(roleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    public RoleResponseDTO create(RoleCreateDTO createDTO) {
        log.debug("Creating new role with name: {}", createDTO.getName());
        
        // Validar se name já existe
        if (roleRepository.existsByName(createDTO.getName())) {
            throw new BusinessException("Nome da role já está em uso: " + createDTO.getName());
        }
        
        // Converter DTO para entidade
        Role role = roleMapper.toEntity(createDTO);
        
        // Salvar role
        Role savedRole = roleRepository.save(role);
        
        log.info("Role created successfully with id: {}", savedRole.getId());
        
        return roleMapper.toResponseDTO(savedRole);
    }
    
    public RoleResponseDTO update(Long id, RoleUpdateDTO updateDTO) {
        log.debug("Updating role with id: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        
        // Validar name único (se está sendo alterado)
        if (updateDTO.getName() != null && !updateDTO.getName().equals(role.getName())) {
            if (roleRepository.existsByName(updateDTO.getName())) {
                throw new BusinessException("Nome da role já está em uso: " + updateDTO.getName());
            }
        }
        
        // Atualizar dados da role
        roleMapper.updateEntityFromDTO(role, updateDTO);
        
        Role savedRole = roleRepository.save(role);
        
        log.info("Role updated successfully with id: {}", savedRole.getId());
        
        return roleMapper.toResponseDTO(savedRole);
    }
    
    public void delete(Long id) {
        log.debug("Deleting role with id: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        
        // Verificar se a role está sendo usada por usuários
        if (!role.getUsers().isEmpty()) {
            throw new BusinessException("Não é possível deletar role que está sendo usada por usuários");
        }
        
        roleRepository.deleteById(id);
        
        log.info("Role deleted successfully with id: {}", id);
    }
} 