package org.example.rawabet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    private String newPassword;
}
