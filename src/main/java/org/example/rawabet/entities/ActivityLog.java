package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * ActivityLog — persiste chaque événement admin en base de données.
 * Remplace le stockage sessionStorage côté front par une vraie persistance.
 */
@Entity
@Table(name = "activity_log", indexes = {
        @Index(name = "idx_activity_timestamp", columnList = "timestamp"),
        @Index(name = "idx_activity_type",      columnList = "type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String type;       // user_login, user_register, user_ban, etc.

    @Column(nullable = false)
    private String message;

    @Column
    private String detail;     // email, info supplémentaire

    @Column(nullable = false, length = 10)
    private String icon;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    @Builder.Default
    private Instant timestamp = Instant.now();
}