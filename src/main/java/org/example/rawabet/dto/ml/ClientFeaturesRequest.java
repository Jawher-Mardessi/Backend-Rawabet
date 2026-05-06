package org.example.rawabet.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientFeaturesRequest {

    @JsonProperty("anciennete_jours")
    private int anciennetéJours;

    @JsonProperty("has_abonnement")
    private int hasAbonnement;

    @JsonProperty("abonnement_type")
    private int abonnementType;

    @JsonProperty("nb_reservations")
    private int nbReservations;

    @JsonProperty("nb_annulations")
    private int nbAnnulations;

    @JsonProperty("taux_annulation")
    private double tauxAnnulation;

    @JsonProperty("nb_resa_30j")
    private int nbResa30j;

    @JsonProperty("points_actuels")
    private int pointsActuels;

    @JsonProperty("level")
    private int level;

    @JsonProperty("total_points_gagnes")
    private int totalPointsGagnes;

    @JsonProperty("points_depenses")
    private int pointsDepenses;

    @JsonProperty("jours_avant_expiration")
    private int joursAvantExpiration;

    @JsonProperty("freq_cinema")
    private int freqCinema;

    @JsonProperty("freq_event")
    private int freqEvent;

    @JsonProperty("freq_club")
    private int freqClub;

    @JsonProperty("freq_bonus")
    private int freqBonus;

    @JsonProperty("nb_feedbacks")
    private int nbFeedbacks;

    @JsonProperty("note_moyenne")
    private double noteMoyenne;

    @JsonProperty("nb_notifications")
    private int nbNotifications;

    @JsonProperty("notif_lues_pct")
    private double notifLuesPct;

    @JsonProperty("montant_total")
    private double montantTotal;

    @JsonProperty("login_failed")
    private int loginFailed;

    @JsonProperty("is_locked")
    private int isLocked;
}