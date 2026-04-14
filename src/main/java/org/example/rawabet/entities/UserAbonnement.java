package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserAbonnement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @ManyToOne
    private Abonnement abonnement;

    private int ticketsRestants;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    public void setAbonnement(Abonnement abonnement) {
        this.abonnement = abonnement;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTicketsRestants(int ticketsRestants) {
        this.ticketsRestants = ticketsRestants;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Abonnement getAbonnement() {
        return abonnement;
    }

    public int getTicketsRestants() {
        return ticketsRestants;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }
}