package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private int numero;

    @ManyToOne
    private Seance seance;

    @ManyToOne
    private ReservationCinema reservation;

}