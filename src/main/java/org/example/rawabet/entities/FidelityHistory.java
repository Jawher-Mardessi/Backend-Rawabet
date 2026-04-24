package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.ActionType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FidelityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ActionType action;

    private int points;

    // CORRECTION — @CreationTimestamp : Hibernate remplit automatiquement
    // Avant : renseigné manuellement via Instant.now() dans saveHistory(),
    // risque d'oubli si on crée un FidelityHistory ailleurs
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;
}