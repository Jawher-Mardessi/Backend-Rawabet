package org.example.rawabet.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ReservationMateriel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantite;

    @ManyToOne
    private ReservationEvenement reservationEvenement;

    @ManyToOne
    private Materiel materiel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public ReservationEvenement getReservationEvenement() {
        return reservationEvenement;
    }

    public void setReservationEvenement(ReservationEvenement reservationEvenement) {
        this.reservationEvenement = reservationEvenement;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }
}