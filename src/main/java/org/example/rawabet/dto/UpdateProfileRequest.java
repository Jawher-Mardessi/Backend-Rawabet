package org.example.rawabet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    private String email;
}