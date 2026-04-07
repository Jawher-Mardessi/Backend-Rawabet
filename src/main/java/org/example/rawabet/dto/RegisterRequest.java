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

    public @NotBlank(message = "Nom obligatoire") String getNom() {
        return nom;
    }

    public void setNom(@NotBlank(message = "Nom obligatoire") String nom) {
        this.nom = nom;
    }

    public @Email(message = "Email invalide") @NotBlank(message = "Email obligatoire") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Email invalide") @NotBlank(message = "Email obligatoire") String email) {
        this.email = email;
    }

    public @Size(min = 6, message = "Password doit contenir au moins 6 caractères") String getPassword() {
        return password;
    }

    public void setPassword(@Size(min = 6, message = "Password doit contenir au moins 6 caractères") String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}