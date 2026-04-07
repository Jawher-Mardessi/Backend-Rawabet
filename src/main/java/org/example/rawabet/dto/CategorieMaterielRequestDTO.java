package org.example.rawabet.dto;

import jakarta.validation.constraints.*;

public class CategorieMaterielRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String description;

    public CategorieMaterielRequestDTO() {}

    public CategorieMaterielRequestDTO(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}