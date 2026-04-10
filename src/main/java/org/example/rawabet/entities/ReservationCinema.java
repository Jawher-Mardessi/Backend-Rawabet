package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.rawabet.cinema.entities.Seat;
import org.example.rawabet.enums.ReservationStatus;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateReservation;

    @Enumerated(EnumType.STRING)
    private ReservationStatus statut;

    @ManyToOne
    private User user;

    @ManyToOne
    private Seance seance;

    @ManyToOne
    private Seat seat;

    @OneToOne
    private Paiement paiement;
}