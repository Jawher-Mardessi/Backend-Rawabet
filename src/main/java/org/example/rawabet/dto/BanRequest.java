package org.example.rawabet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO pour le ban temporaire d'un utilisateur.
 *
 * Exemples :
 *   { "banUntil": "2026-05-01T00:00:00", "reason": "Comportement inapproprié" }
 *   { "banUntil": null, "reason": "Ban permanent" }
 */
@Data
public class BanRequest {

    /**
     * Date/heure de fin de ban.
     * null = ban permanent (isActive = false, banUntil = null)
     * non-null = ban temporaire (isActive = false, banUntil = date)
     */
    private LocalDateTime banUntil;

    /**
     * Raison du ban (obligatoire).
     */
    @NotBlank(message = "La raison du ban est obligatoire")
    private String reason;
}