package org.example.rawabet.dto;

import java.time.LocalDateTime;

public class MaterielOccupationDTO {

    private String type;           // "RESERVATION" or "EVENEMENT"
    private int quantite;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    // For RESERVATION type
    private Long reservationId;
    private String userNom;
    private String statut;

    // For EVENEMENT type
    private Long evenementId;
    private String evenementTitre;

    public MaterielOccupationDTO() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getUserNom() { return userNom; }
    public void setUserNom(String userNom) { this.userNom = userNom; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Long getEvenementId() { return evenementId; }
    public void setEvenementId(Long evenementId) { this.evenementId = evenementId; }

    public String getEvenementTitre() { return evenementTitre; }
    public void setEvenementTitre(String evenementTitre) { this.evenementTitre = evenementTitre; }
}