package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.AbonnementType;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
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

    public Long getId() {
        return id;
    }

    public AbonnementType getType() {
        return type;
    }

    public String getNom() {
        return nom;
    }

    public int getNbTicketsParMois() {
        return nbTicketsParMois;
    }

    public boolean isIllimite() {
        return illimite;
    }

    public boolean isPopcornGratuit() {
        return popcornGratuit;
    }

    public double getPrix() {
        return prix;
    }

    public void setType(AbonnementType type) {
        this.type = type;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setNbTicketsParMois(int nbTicketsParMois) {
        this.nbTicketsParMois = nbTicketsParMois;
    }

    public void setIllimite(boolean illimite) {
        this.illimite = illimite;
    }

    public void setPopcornGratuit(boolean popcornGratuit) {
        this.popcornGratuit = popcornGratuit;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }
}