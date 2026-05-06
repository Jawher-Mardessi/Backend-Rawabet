package org.example.rawabet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.rawabet.enums.SubscriptionStatus;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class SubscribeResponse {
    private Long userId;
    private Long subscriptionId;
    private Long abonnementId;
    private String abonnementType;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int ticketsRestants;
    private SubscriptionStatus status;
    private String resultType;  // ACTIVATED_NOW or QUEUED_NEXT
    private String message;
}
