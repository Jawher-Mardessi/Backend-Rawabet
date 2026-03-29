package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFacture;

    private LocalDate dateEmission;

    private double montant;

    @OneToOne
    @JoinColumn(name="paiement_id")
    private Paiement paiement;

}
