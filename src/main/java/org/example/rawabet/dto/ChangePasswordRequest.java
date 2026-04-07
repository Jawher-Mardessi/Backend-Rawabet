package org.example.rawabet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Ancien mot de passe obligatoire")
    private String oldPassword;

    @Size(min = 6, message = "Nouveau mot de passe minimum 6 caractères")
    @NotBlank(message = "Nouveau mot de passe obligatoire")
    private String newPassword;
}