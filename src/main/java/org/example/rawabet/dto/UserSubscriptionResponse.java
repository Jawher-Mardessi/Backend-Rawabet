package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserSubscriptionResponse {

    private Long userId;
    private Long userAbonnementId;
    private Long abonnementId;
    private String abonnementNom;
    private String abonnementType;
    private int ticketsRestants;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
