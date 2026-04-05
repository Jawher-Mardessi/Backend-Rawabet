package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.ActionType;

import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FidelityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 lien user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 🔥 type action
    @Enumerated(EnumType.STRING)
    private ActionType action;

    // 🔥 points ajoutés
    private int points;

    // 🔥 date
    private Instant createdAt;
}