package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.AbonnementType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Abonnement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AbonnementType type;

    private String nom;

    private int nbTicketsParMois; // 2, 5 ou 0

    private boolean illimite;

    private boolean popcornGratuit;

    private double prix;

    @JsonIgnore
    @OneToMany(mappedBy = "abonnement")
    private List<UserAbonnement> subscriptions;

    // Constructor for initialization (without subscriptions)
    public Abonnement(Long id, AbonnementType type, String nom, int nbTicketsParMois,
                      boolean illimite, boolean popcornGratuit, double prix) {
        this.id = id;
        this.type = type;
        this.nom = nom;
        this.nbTicketsParMois = nbTicketsParMois;
        this.illimite = illimite;
        this.popcornGratuit = popcornGratuit;
        this.prix = prix;
    }
}