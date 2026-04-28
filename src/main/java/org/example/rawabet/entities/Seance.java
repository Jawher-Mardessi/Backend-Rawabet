package org.example.rawabet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.cinema.entities.Film;
import org.example.rawabet.cinema.entities.Seat;
import org.example.rawabet.cinema.entities.SalleCinema;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Seance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateHeure;
    private double prixBase;
    private String langue;

    @ManyToOne
    private Film film;

    @ManyToOne
    private SalleCinema salleCinema;

    @OneToMany(mappedBy = "seance")
    @JsonIgnore
    private List<ReservationCinema> reservations;

    @OneToMany
    @JoinColumn(name = "seance_id")
    @JsonIgnore
    private List<Seat> seats;
}
