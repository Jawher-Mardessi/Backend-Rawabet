package org.example.rawabet.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.rawabet.enums.PaymentStatus;

import java.time.LocalDate;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Paiement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double montant;
    private java.time.LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    private PaymentStatus statut;

    @OneToOne(mappedBy = "paiement")
    private ReservationCinema reservationCinema;

    @OneToOne(mappedBy = "paiement")
    private ReservationEvenement reservationEvenement;

    @OneToOne
    private Facture facture;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public PaymentStatus getStatut() {
        return statut;
    }

    public void setStatut(PaymentStatus statut) {
        this.statut = statut;
    }

    public ReservationCinema getReservationCinema() {
        return reservationCinema;
    }

    public void setReservationCinema(ReservationCinema reservationCinema) {
        this.reservationCinema = reservationCinema;
    }

    public ReservationEvenement getReservationEvenement() {
        return reservationEvenement;
    }

    public void setReservationEvenement(ReservationEvenement reservationEvenement) {
        this.reservationEvenement = reservationEvenement;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }
}