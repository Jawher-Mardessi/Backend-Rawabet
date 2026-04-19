package org.example.rawabet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.UserAbonnementStatus;

import java.time.LocalDate;

/**
 * Represents one purchased subscription period for a user.
 * A user may have multiple records: one ACTIVE and zero-or-more QUEUED,
 * plus historical EXPIRED/EXHAUSTED entries that are never deleted.
 */
@Entity
@Table(name = "user_abonnement")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class UserAbonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /** The plan template that was purchased. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "abonnement_id", nullable = false)
    private Abonnement abonnement;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAbonnementStatus status;

    /**
     * Remaining ticket count for this subscription period.
     * -1 means unlimited (derived from the plan's {@code ticketsMax}).
     */
    @Column(nullable = false)
    private int ticketsRestants;

    @OneToOne(mappedBy = "userAbonnement", cascade = CascadeType.ALL, orphanRemoval = true)
    private QRCode qrCode;
}
