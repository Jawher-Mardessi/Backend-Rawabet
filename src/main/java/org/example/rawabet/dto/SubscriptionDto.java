package org.example.rawabet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.rawabet.enums.SubscriptionStatus;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class SubscriptionDto {
    private Long subscriptionId;
    private Long abonnementId;
    private String abonnementType;
    private String abonnementName;
    private String qrCode;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int ticketsRestants;
    private boolean isIllimited;
    private SubscriptionStatus status;
}
