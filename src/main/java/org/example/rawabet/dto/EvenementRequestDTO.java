package org.example.rawabet.dto;

import jakarta.validation.constraints.*;
import org.example.rawabet.enums.EvenementStatus;
import org.example.rawabet.enums.TypeCategorie;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class EvenementRequestDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime dateFin;

    @Min(value = 1, message = "Le nombre de places doit être supérieur à 0")
    private int nombreDePlaces;

    @NotNull(message = "La salle est obligatoire")
    private Long salleId;

    private EvenementStatus status;

    @NotNull(message = "La catégorie est obligatoire")
    private TypeCategorie categorie;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prixUnitaire;

    private BigDecimal prixReduit;

    public EvenementRequestDTO() {}

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public int getNombreDePlaces() { return nombreDePlaces; }
    public void setNombreDePlaces(int nombreDePlaces) { this.nombreDePlaces = nombreDePlaces; }

    public Long getSalleId() { return salleId; }
    public void setSalleId(Long salleId) { this.salleId = salleId; }

    public EvenementStatus getStatus() { return status; }
    public void setStatus(EvenementStatus status) { this.status = status; }

    public TypeCategorie getCategorie() { return categorie; }
    public void setCategorie(TypeCategorie categorie) { this.categorie = categorie; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }


}