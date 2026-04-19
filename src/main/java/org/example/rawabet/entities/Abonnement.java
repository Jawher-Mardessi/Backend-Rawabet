package org.example.rawabet.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.rawabet.enums.AbonnementType;

/**
 * Plan template – defines what a subscription offers.
 * Actual per-user subscriptions are stored in {@link UserAbonnement}.
 */
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Abonnement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AbonnementType type;

    private double prix;

    /** Duration of the subscription in months (e.g. 1 for MENSUEL, 12 for ANNUEL). */
    private int dureeEnMois;

    /**
     * Maximum number of tickets included in this plan.
     * Use -1 to indicate an unlimited plan.
     */
    private int ticketsMax;

    private String description;

    /** @return true if this plan has no ticket limit */
    public boolean isUnlimited() {
        return ticketsMax < 0;
    }
}