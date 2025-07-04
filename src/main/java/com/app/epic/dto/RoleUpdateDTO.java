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
public class RoleUpdateDTO {
    
    @Size(min = 2, max = 50, message = "Nome da role deve ter entre 2 e 50 caracteres")
    @Pattern(regexp = "^[A-Z_]+$", message = "Nome da role deve conter apenas letras maiúsculas e underscore")
    private String name;
    
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String description;
} 