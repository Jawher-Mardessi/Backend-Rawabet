package org.example.rawabet.dto;

import jakarta.validation.constraints.*;
import org.example.rawabet.enums.SalleStatus;
import org.example.rawabet.enums.SalleType;

public class SalleEvenementRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Min(value = 1, message = "La capacité doit être supérieure à 0")
    private int capacite;

    @NotNull(message = "Le type est obligatoire")
    private SalleType type;

    @NotNull(message = "Le status est obligatoire")
    private SalleStatus status;

    public SalleEvenementRequestDTO() {}

    public SalleEvenementRequestDTO(String nom, int capacite, SalleType type, SalleStatus status) {
        this.nom = nom;
        this.capacite = capacite;
        this.type = type;
        this.status = status;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public SalleType getType() { return type; }
    public void setType(SalleType type) { this.type = type; }

    public SalleStatus getStatus() { return status; }
    public void setStatus(SalleStatus status) { this.status = status; }
}