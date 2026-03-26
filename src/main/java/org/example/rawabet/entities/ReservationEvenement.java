package org.example.rawabet.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.rawabet.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ReservationEvenement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private java.time.LocalDateTime dateDebut;
    private java.time.LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private ReservationStatus statut;

    @ManyToOne
    private User user;

    @ManyToOne
    private Evenement evenement;

    @OneToMany(mappedBy = "reservationEvenement")
    @JsonIgnore
    private List<ReservationMateriel> materiels;

    @OneToOne
    private Paiement paiement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ReservationStatus getStatut() {
        return statut;
    }

    public void setStatut(ReservationStatus statut) {
        this.statut = statut;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public List<ReservationMateriel> getMateriels() {
        return materiels;
    }

    public void setMateriels(List<ReservationMateriel> materiels) {
        this.materiels = materiels;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }
}