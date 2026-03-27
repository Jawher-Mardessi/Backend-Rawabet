package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.PaymentStatus;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double montant;

    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    private PaymentStatus statut;

    private String methode; // CARD / CASH / ONLINE

    // Paiement reservation cinema
    @OneToOne
    @JoinColumn(name = "reservation_cinema_id")
    private ReservationCinema reservationCinema;

    // Paiement reservation evenement
    @OneToOne
    @JoinColumn(name = "reservation_evenement_id")
    private ReservationEvenement reservationEvenement;

    // Facture
    @OneToOne(mappedBy = "paiement", cascade = CascadeType.ALL)
    private Facture facture;

}