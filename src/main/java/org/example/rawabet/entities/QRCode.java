package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class QRCode {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // UUID unique

    @OneToOne
    @JoinColumn(name = "user_abonnement_id", nullable = false)
    private UserAbonnement userAbonnement;

    private boolean used; // false -> not scanned, true -> already used

    private LocalDateTime createdAt;

    private LocalDateTime scannedAt; // When QR was scanned
}