package org.example.rawabet.dto;

import org.example.rawabet.enums.ReservationStatus;
import java.time.LocalDateTime;

public class ReservationEvenementResponseDTO {

    private Long id;
    private LocalDateTime dateReservation;
    private LocalDateTime dateExpiration;
    private ReservationStatus statut;
    private boolean enAttente;
    private Long userId;
    private String userNom;
    private Long evenementId;
    private String evenementTitre;
    private LocalDateTime evenementDateDebut;
    private LocalDateTime evenementDateFin;
    private String salleNom;

    public ReservationEvenementResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }

    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }

    public ReservationStatus getStatut() { return statut; }
    public void setStatut(ReservationStatus statut) { this.statut = statut; }

    public boolean isEnAttente() { return enAttente; }
    public void setEnAttente(boolean enAttente) { this.enAttente = enAttente; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserNom() { return userNom; }
    public void setUserNom(String userNom) { this.userNom = userNom; }

    public Long getEvenementId() { return evenementId; }
    public void setEvenementId(Long evenementId) { this.evenementId = evenementId; }

    public String getEvenementTitre() { return evenementTitre; }
    public void setEvenementTitre(String evenementTitre) { this.evenementTitre = evenementTitre; }

    public LocalDateTime getEvenementDateDebut() { return evenementDateDebut; }
    public void setEvenementDateDebut(LocalDateTime evenementDateDebut) { this.evenementDateDebut = evenementDateDebut; }

    public LocalDateTime getEvenementDateFin() { return evenementDateFin; }
    public void setEvenementDateFin(LocalDateTime evenementDateFin) { this.evenementDateFin = evenementDateFin; }

    public String getSalleNom() { return salleNom; }
    public void setSalleNom(String salleNom) { this.salleNom = salleNom; }
}