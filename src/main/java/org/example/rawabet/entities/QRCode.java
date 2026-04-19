package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * QR code attached to an active {@link UserAbonnement}.
 * Generated on first activation; used for physical ticket scanning.
 */
@Entity
@Table(name = "qr_code")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class QRCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_abonnement_id", nullable = false, unique = true)
    private UserAbonnement userAbonnement;

    /** Unique token used for physical scanning (UUID). */
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private LocalDateTime generatedAt;
}
