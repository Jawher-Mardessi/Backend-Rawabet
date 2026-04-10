package org.example.rawabet.entities;
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

    private java.time.LocalDateTime dateHeure;
    private double prixBase;
    private String langue;

    @ManyToOne
    private Film film;

    @ManyToOne
    private SalleCinema salleCinema;

    @OneToMany(mappedBy = "seance")
    private List<ReservationCinema> reservations;

    @OneToMany
    @JoinColumn(name = "seance_id")
    private List<org.example.rawabet.cinema.entities.Seat> seats;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public double getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(double prixBase) {
        this.prixBase = prixBase;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public SalleCinema getSalleCinema() {
        return salleCinema;
    }

    public void setSalleCinema(SalleCinema salleCinema) {
        this.salleCinema = salleCinema;
    }

    public List<ReservationCinema> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationCinema> reservations) {
        this.reservations = reservations;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
