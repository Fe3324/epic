package com.app.epic.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username deve conter apenas letras, números, pontos, underscore e hífen")
    private String username;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    private String email;
    
    @NotBlank(message = "Password é obrigatório")
    @Size(min = 6, max = 100, message = "Password deve ter entre 6 e 100 caracteres")
    private String password;
    
    @NotBlank(message = "Confirmação de password é obrigatória")
    private String confirmPassword;
    
    @Size(max = 100, message = "Primeiro nome deve ter no máximo 100 caracteres")
    private String firstName;
    
    @Size(max = 100, message = "Sobrenome deve ter no máximo 100 caracteres")
    private String lastName;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Telefone deve ter formato internacional válido")
    private String phone;
    
    @Past(message = "Data de nascimento deve estar no passado")
    private LocalDate birthDate;
    
    // Validação customizada para confirmar senha
    @AssertTrue(message = "Password e confirmação devem ser iguais")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
} 