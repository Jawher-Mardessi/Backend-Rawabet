package org.example.rawabet.dto;

import jakarta.validation.constraints.*;
import org.example.rawabet.enums.MaterielStatus;

public class MaterielRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String description;

    @NotBlank(message = "La référence est obligatoire")
    private String reference;

    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private int quantiteDisponible;

    @DecimalMin(value = "0.0", message = "Le prix ne peut pas être négatif")
    private double prixUnitaire;

    private boolean disponible;

    private MaterielStatus status;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;

    public MaterielRequestDTO() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public int getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(int quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public MaterielStatus getStatus() { return status; }
    public void setStatus(MaterielStatus status) { this.status = status; }

    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }
}