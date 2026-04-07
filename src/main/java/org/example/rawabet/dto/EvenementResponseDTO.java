package org.example.rawabet.dto;

import org.example.rawabet.enums.EvenementStatus;
import org.example.rawabet.enums.SalleType;
import java.time.LocalDateTime;

public class EvenementResponseDTO {

    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int nombreDePlaces;
    private int placesRestantes;
    private EvenementStatus status;
    private Long salleId;
    private String salleNom;
    private SalleType salleType;

    public EvenementResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public int getPlacesRestantes() { return placesRestantes; }
    public void setPlacesRestantes(int placesRestantes) { this.placesRestantes = placesRestantes; }

    public EvenementStatus getStatus() { return status; }
    public void setStatus(EvenementStatus status) { this.status = status; }

    public Long getSalleId() { return salleId; }
    public void setSalleId(Long salleId) { this.salleId = salleId; }

    public String getSalleNom() { return salleNom; }
    public void setSalleNom(String salleNom) { this.salleNom = salleNom; }

    public SalleType getSalleType() { return salleType; }
    public void setSalleType(SalleType salleType) { this.salleType = salleType; }
}