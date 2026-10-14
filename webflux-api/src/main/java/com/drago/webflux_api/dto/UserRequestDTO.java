package com.drago.webflux_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
    @NotBlank(message = "O nome não pode estar em branco") 
    String name,
    
    @NotBlank(message = "O email é obrigatório") 
    @Email(message = "Formato de email inválido") 
    String email,

    @NotBlank(message = "A senha é obrigatória")
    String password
) {}