package org.example.rawabet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserResponse {

    private Long   id;
    private String nom;
    private String email;
    private String avatarUrl;
    private List<String> roles;
    private boolean isActive;
    private String loyaltyLevel;
    private Integer loyaltyPoints;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // ── Ban temporaire ─────────────────────────────────────────────────────
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime banUntil;

    private String banReason;

    // ── Sécurité ───────────────────────────────────────────────────────────
    private int loginFailedAttempts;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime loginLockedUntil;
}