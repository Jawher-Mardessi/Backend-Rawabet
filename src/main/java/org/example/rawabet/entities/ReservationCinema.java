package org.example.rawabet.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.rawabet.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ReservationCinema {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private java.time.LocalDate dateReservation;

    @Enumerated(EnumType.STRING)
    private ReservationStatus statut;

    @ManyToOne
    private User user;

    @ManyToOne
    private Seance seance;

    @OneToMany(mappedBy = "reservationCinema")
    private List<Ticket> tickets;

    @OneToOne
    private Paiement paiement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
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

    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }
}
