package com.app.epic.mapper;

import com.app.epic.dto.AddressCreateDTO;
import com.app.epic.dto.AddressResponseDTO;
import com.app.epic.dto.AddressUpdateDTO;
import com.app.epic.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    
    public AddressResponseDTO toResponseDTO(Address address) {
        if (address == null) {
            return null;
        }
        
        return AddressResponseDTO.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .isPrimary(address.getIsPrimary())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
    
    public Address toEntity(AddressCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        
        return Address.builder()
                .street(createDTO.getStreet())
                .city(createDTO.getCity())
                .state(createDTO.getState())
                .zipCode(createDTO.getZipCode())
                .country(createDTO.getCountry() != null ? createDTO.getCountry() : "Brasil")
                .isPrimary(createDTO.getIsPrimary() != null ? createDTO.getIsPrimary() : false)
                .build();
    }
    
    public void updateEntityFromDTO(Address address, AddressUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return;
        }
        
        if (updateDTO.getStreet() != null) {
            address.setStreet(updateDTO.getStreet());
        }
        if (updateDTO.getCity() != null) {
            address.setCity(updateDTO.getCity());
        }
        if (updateDTO.getState() != null) {
            address.setState(updateDTO.getState());
        }
        if (updateDTO.getZipCode() != null) {
            address.setZipCode(updateDTO.getZipCode());
        }
        if (updateDTO.getCountry() != null) {
            address.setCountry(updateDTO.getCountry());
        }
        if (updateDTO.getIsPrimary() != null) {
            address.setIsPrimary(updateDTO.getIsPrimary());
        }
    }
} 