package org.example.rawabet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateHeure;

    private double prixBase;

    private String langue;

    @ManyToOne
    private Film film;

    @ManyToOne
    private SalleCinema salleCinema;

    @OneToMany(mappedBy="seance", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ReservationCinema> reservations;

    @OneToMany(mappedBy="seance", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Seat> seats;

}