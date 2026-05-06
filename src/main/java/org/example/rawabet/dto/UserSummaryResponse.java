package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO allégé pour les listes admin (GET /users/all).
 *
 * N'embarque PAS les données fidélité (loyaltyLevel, loyaltyPoints)
 * pour éviter le problème N+1 queries dans getAllUsers().
 *
 * Pour le profil complet avec fidélité → utiliser UserResponse.
 */
@Data
@Builder
public class UserSummaryResponse {
    private Long id;
    private String nom;
    private String email;
    private String avatarUrl;
    private List<String> roles;
    private boolean isActive;
    private LocalDateTime createdAt;
}