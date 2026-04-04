package org.example.rawabet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    private String email;

    @Size(min = 6, message = "Password doit contenir au moins 6 caractères")
    private String password;
    private List<String> roles;
}