package org.example.rawabet.dto.abonnement;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.AbonnementType;
import org.example.rawabet.enums.UserAbonnementStatus;

import java.time.LocalDate;

@Data
@Builder
public class UserAbonnementDTO {

    private Long id;
    private Long userId;
    private Long abonnementId;
    private AbonnementType abonnementType;
    private String abonnementDescription;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int ticketsRestants;
    private UserAbonnementStatus status;
    private String qrCode;
}
