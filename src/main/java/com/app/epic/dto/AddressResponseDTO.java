package com.app.epic.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    
    private Long id;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Método utilitário
    public String getFullAddress() {
        return String.format("%s, %s, %s - %s, %s", 
            street, city, state, zipCode, country);
    }
} 