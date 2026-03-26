package org.example.rawabet.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Evenement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private java.time.LocalDateTime dateDebut;
    private java.time.LocalDateTime dateFin;

    @ManyToOne
    private SalleEvenement salle;

    @OneToMany(mappedBy = "evenement")
    private List<ReservationEvenement> reservations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public SalleEvenement getSalle() {
        return salle;
    }

    public void setSalle(SalleEvenement salle) {
        this.salle = salle;
    }

    public List<ReservationEvenement> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationEvenement> reservations) {
        this.reservations = reservations;
    }
}
