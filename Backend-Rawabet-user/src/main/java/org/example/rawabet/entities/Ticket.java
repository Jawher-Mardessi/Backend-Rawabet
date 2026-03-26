package org.example.rawabet.entities;
import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.TicketStatus;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TicketStatus statut;

    @ManyToOne
    private ReservationCinema reservationCinema;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TicketStatus getStatut() {
        return statut;
    }

    public void setStatut(TicketStatus statut) {
        this.statut = statut;
    }

    public ReservationCinema getReservationCinema() {
        return reservationCinema;
    }

    public void setReservationCinema(ReservationCinema reservationCinema) {
        this.reservationCinema = reservationCinema;
    }
}