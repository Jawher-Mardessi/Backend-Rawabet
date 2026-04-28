package org.example.rawabet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO dédié à la mise à jour d'un utilisateur par un admin.
 *
 * Séparé de RegisterRequest pour éviter l'ambiguïté :
 * - Le champ `password` est optionnel (null = ne pas changer)
 * - Le champ `roles` n'existe pas ici (géré via PUT /{id}/roles)
 * - @NotBlank sur password si fourni, via validation manuelle dans le service
 */
@Data
public class AdminUpdateUserRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    private String email;

    /**
     * Optionnel. Si fourni, doit contenir au moins 6 caractères non-blancs.
     * Laisser null pour ne pas modifier le mot de passe.
     */
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
}