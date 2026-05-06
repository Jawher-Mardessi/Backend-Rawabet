package org.example.rawabet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminActivityEvent {

    private String type;      // user_login, user_register, face_login, suspect_login, user_ban, loyalty_upgrade
    private String message;   // Message affiché
    private String detail;    // Détail (email, info supplémentaire)
    private String icon;      // Emoji
    private String color;     // Classes CSS Tailwind

    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();

    // ── Factory methods ───────────────────────────────────────────────

    public static AdminActivityEvent userLogin(String email) {
        return AdminActivityEvent.builder()
                .type("user_login")
                .message("Connexion utilisateur")
                .detail(email)
                .icon("👤")
                .color("bg-gray-50 border-gray-200 text-gray-700")
                .build();
    }

    public static AdminActivityEvent userRegister(String email) {
        return AdminActivityEvent.builder()
                .type("user_register")
                .message("Nouvel utilisateur inscrit")
                .detail(email)
                .icon("🟢")
                .color("bg-green-50 border-green-200 text-green-700")
                .build();
    }

    public static AdminActivityEvent faceLogin(String email) {
        return AdminActivityEvent.builder()
                .type("face_login")
                .message("Connexion par visage")
                .detail(email)
                .icon("📸")
                .color("bg-blue-50 border-blue-200 text-blue-700")
                .build();
    }

    public static AdminActivityEvent suspectLogin(String email, String ip) {
        return AdminActivityEvent.builder()
                .type("suspect_login")
                .message("Tentative suspecte détectée")
                .detail(email + (ip != null ? " — IP: " + ip : ""))
                .icon("🔴")
                .color("bg-red-50 border-red-200 text-red-700")
                .build();
    }

    public static AdminActivityEvent userBan(String email) {
        return AdminActivityEvent.builder()
                .type("user_ban")
                .message("Compte suspendu")
                .detail(email)
                .icon("🚫")
                .color("bg-red-50 border-red-200 text-red-700")
                .build();
    }

    public static AdminActivityEvent loyaltyUpgrade(String name, String newLevel) {
        return AdminActivityEvent.builder()
                .type("loyalty_upgrade")
                .message("Passage niveau " + newLevel.toUpperCase())
                .detail(name + " → " + newLevel)
                .icon("🥇")
                .color("bg-yellow-50 border-yellow-200 text-yellow-700")
                .build();
    }

    public static AdminActivityEvent loyaltyPoints(String name, int points) {
        return AdminActivityEvent.builder()
                .type("loyalty_points")
                .message("+" + points + " points fidélité")
                .detail(name)
                .icon("⭐")
                .color("bg-purple-50 border-purple-200 text-purple-700")
                .build();
    }
}