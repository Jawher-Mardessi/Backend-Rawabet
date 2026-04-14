package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class QRCode {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_abonnement_id", nullable = false)
    private UserAbonnement userAbonnement;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime scannedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}