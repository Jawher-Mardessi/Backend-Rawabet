package org.example.rawabet.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ReservationMaterielRequestDTO {

    @NotNull(message = "L'utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "Le matériel est obligatoire")
    private Long materielId;

    @Min(value = 1, message = "La quantité doit être supérieure à 0")
    private int quantite;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime dateFin;

    public ReservationMaterielRequestDTO() {}

    public ReservationMaterielRequestDTO(Long userId, Long materielId, int quantite,
                                         LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.userId = userId;
        this.materielId = materielId;
        this.quantite = quantite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
}