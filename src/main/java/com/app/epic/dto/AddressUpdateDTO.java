package com.app.epic.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateDTO {
    
    @Size(max = 255, message = "Rua deve ter no máximo 255 caracteres")
    private String street;
    
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String city;
    
    @Size(max = 100, message = "Estado deve ter no máximo 100 caracteres")
    private String state;
    
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve ter formato válido (12345-678)")
    private String zipCode;
    
    @Size(max = 100, message = "País deve ter no máximo 100 caracteres")
    private String country;
    
    private Boolean isPrimary;
} 