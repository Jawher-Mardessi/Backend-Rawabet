package org.example.rawabet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

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

    @OneToMany(mappedBy="reservation", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Seat> seats;

    @OneToOne(mappedBy="reservationCinema")
    private Paiement paiement;

}