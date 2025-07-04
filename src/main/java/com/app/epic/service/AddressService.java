package com.app.epic.service;

import com.app.epic.dto.*;
import com.app.epic.entity.Address;
import com.app.epic.entity.User;
import com.app.epic.exception.BusinessException;
import com.app.epic.exception.ResourceNotFoundException;
import com.app.epic.mapper.AddressMapper;
import com.app.epic.repository.AddressRepository;
import com.app.epic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    
    @Transactional(readOnly = true)
    public PageResponse<AddressResponseDTO> findAll(Pageable pageable) {
        log.debug("Finding all addresses with pagination: {}", pageable);
        
        Page<Address> addressPage = addressRepository.findAll(pageable);
        
        List<AddressResponseDTO> content = addressPage.getContent()
                .stream()
                .map(addressMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(content, addressPage.getNumber(), addressPage.getSize(), addressPage.getTotalElements());
    }
    
    @Transactional(readOnly = true)
    public AddressResponseDTO findById(Long id) {
        log.debug("Finding address by id: {}", id);
        
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço", "id", id));
        
        return addressMapper.toResponseDTO(address);
    }
    
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> findByUserId(Long userId) {
        log.debug("Finding addresses by user id: {}", userId);
        
        // Verificar se o usuário existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário", "id", userId);
        }
        
        List<Address> addresses = addressRepository.findByUserId(userId);
        
        return addresses.stream()
                .map(addressMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<AddressResponseDTO> findPrimaryAddressByUserId(Long userId) {
        log.debug("Finding primary address by user id: {}", userId);
        
        // Verificar se o usuário existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário", "id", userId);
        }
        
        Optional<Address> primaryAddress = addressRepository.findPrimaryAddressByUserId(userId);
        
        return primaryAddress.map(addressMapper::toResponseDTO);
    }
    
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> findByCity(String city) {
        log.debug("Finding addresses by city: {}", city);
        
        List<Address> addresses = addressRepository.findByCity(city);
        
        return addresses.stream()
                .map(addressMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> findByState(String state) {
        log.debug("Finding addresses by state: {}", state);
        
        List<Address> addresses = addressRepository.findByState(state);
        
        return addresses.stream()
                .map(addressMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> searchByLocation(String city, String state) {
        log.debug("Searching addresses by location - city: {}, state: {}", city, state);
        
        List<Address> addresses = addressRepository.findByLocationContaining(city, state);
        
        return addresses.stream()
                .map(addressMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    public AddressResponseDTO create(AddressCreateDTO createDTO) {
        log.debug("Creating new address for user id: {}", createDTO.getUserId());
        
        // Verificar se o usuário existe
        User user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", createDTO.getUserId()));
        
        // Converter DTO para entidade
        Address address = addressMapper.toEntity(createDTO);
        address.setUser(user);
        
        // Se está sendo marcado como principal, remover flag de outras
        if (address.getIsPrimary()) {
            removePrimaryFlagFromUserAddresses(createDTO.getUserId());
        }
        
        // Salvar endereço
        Address savedAddress = addressRepository.save(address);
        
        log.info("Address created successfully with id: {}", savedAddress.getId());
        
        return addressMapper.toResponseDTO(savedAddress);
    }
    
    public AddressResponseDTO update(Long id, AddressUpdateDTO updateDTO) {
        log.debug("Updating address with id: {}", id);
        
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço", "id", id));
        
        // Atualizar dados do endereço
        addressMapper.updateEntityFromDTO(address, updateDTO);
        
        // Se está sendo marcado como principal, remover flag de outras
        if (updateDTO.getIsPrimary() != null && updateDTO.getIsPrimary()) {
            removePrimaryFlagFromUserAddresses(address.getUser().getId());
            address.setIsPrimary(true);
        }
        
        Address savedAddress = addressRepository.save(address);
        
        log.info("Address updated successfully with id: {}", savedAddress.getId());
        
        return addressMapper.toResponseDTO(savedAddress);
    }
    
    public void delete(Long id) {
        log.debug("Deleting address with id: {}", id);
        
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Endereço", "id", id);
        }
        
        addressRepository.deleteById(id);
        
        log.info("Address deleted successfully with id: {}", id);
    }
    
    public AddressResponseDTO setPrimaryAddress(Long userId, Long addressId) {
        log.debug("Setting primary address - user: {}, address: {}", userId, addressId);
        
        // Verificar se o usuário existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário", "id", userId);
        }
        
        // Verificar se o endereço existe e pertence ao usuário
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço", "id", addressId));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException("Endereço não pertence ao usuário especificado");
        }
        
        // Remover flag de principal de outros endereços do usuário
        removePrimaryFlagFromUserAddresses(userId);
        
        // Marcar este endereço como principal
        address.setIsPrimary(true);
        Address savedAddress = addressRepository.save(address);
        
        log.info("Primary address set successfully - user: {}, address: {}", userId, addressId);
        
        return addressMapper.toResponseDTO(savedAddress);
    }
    
    private void removePrimaryFlagFromUserAddresses(Long userId) {
        List<Address> userAddresses = addressRepository.findByUserId(userId);
        
        userAddresses.forEach(addr -> {
            if (addr.getIsPrimary()) {
                addr.setIsPrimary(false);
                addressRepository.save(addr);
            }
        });
    }
} 