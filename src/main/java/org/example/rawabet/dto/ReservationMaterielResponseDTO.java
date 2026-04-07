package org.example.rawabet.dto;

import org.example.rawabet.enums.ReservationStatus;
import java.time.LocalDateTime;

public class ReservationMaterielResponseDTO {

    private Long id;
    private int quantite;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private ReservationStatus statut;
    private Long userId;
    private String userNom;
    private Long materielId;
    private String materielNom;
    private String materielReference;
    private double prixUnitaire;

    public ReservationMaterielResponseDTO() {}

    public ReservationMaterielResponseDTO(Long id, int quantite, LocalDateTime dateDebut,
                                          LocalDateTime dateFin, ReservationStatus statut,
                                          Long userId, String userNom, Long materielId,
                                          String materielNom, String materielReference,
                                          double prixUnitaire) {
        this.id = id;
        this.quantite = quantite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.userId = userId;
        this.userNom = userNom;
        this.materielId = materielId;
        this.materielNom = materielNom;
        this.materielReference = materielReference;
        this.prixUnitaire = prixUnitaire;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public ReservationStatus getStatut() { return statut; }
    public void setStatut(ReservationStatus statut) { this.statut = statut; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserNom() { return userNom; }
    public void setUserNom(String userNom) { this.userNom = userNom; }

    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public String getMaterielNom() { return materielNom; }
    public void setMaterielNom(String materielNom) { this.materielNom = materielNom; }

    public String getMaterielReference() { return materielReference; }
    public void setMaterielReference(String materielReference) { this.materielReference = materielReference; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}