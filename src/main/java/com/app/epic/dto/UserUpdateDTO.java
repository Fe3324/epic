package com.app.epic.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username deve conter apenas letras, números, pontos, underscore e hífen")
    private String username;
    
    @Email(message = "Email deve ter formato válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    private String email;
    
    @Size(min = 6, max = 100, message = "Password deve ter entre 6 e 100 caracteres")
    private String password;
    
    @Size(max = 100, message = "Primeiro nome deve ter no máximo 100 caracteres")
    private String firstName;
    
    @Size(max = 100, message = "Último nome deve ter no máximo 100 caracteres")
    private String lastName;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$|^$", message = "Telefone deve ter formato válido")
    private String phone;
    
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate birthDate;
    
    private Boolean enabled;
    
    private Set<Long> roleIds;
} 