package org.example.rawabet.dto;

import jakarta.validation.constraints.*;

public class EvenementMaterielRequestDTO {

    @NotNull(message = "L'événement est obligatoire")
    private Long evenementId;

    @NotNull(message = "Le matériel est obligatoire")
    private Long materielId;

    @Min(value = 1, message = "La quantité doit être supérieure à 0")
    private int quantite;

    public EvenementMaterielRequestDTO() {}

    public EvenementMaterielRequestDTO(Long evenementId, Long materielId, int quantite) {
        this.evenementId = evenementId;
        this.materielId = materielId;
        this.quantite = quantite;
    }

    public Long getEvenementId() { return evenementId; }
    public void setEvenementId(Long evenementId) { this.evenementId = evenementId; }

    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
}
