package org.example.rawabet.dto.abonnement;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.AbonnementType;
import org.example.rawabet.enums.SubscribeResultType;
import org.example.rawabet.enums.UserAbonnementStatus;

import java.time.LocalDate;

@Data
@Builder
public class SubscribeResponseDTO {

    private Long userId;
    private Long subscriptionId;
    private Long abonnementId;
    private AbonnementType abonnementType;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int ticketsRestants;
    private UserAbonnementStatus status;

    /** Tells the frontend whether this purchase starts immediately or is queued. */
    private SubscribeResultType resultType;

    private String message;
}
