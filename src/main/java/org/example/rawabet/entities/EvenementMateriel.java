package org.example.rawabet.entities;

import jakarta.persistence.*;

@Entity
public class EvenementMateriel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantite;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @ManyToOne
    @JoinColumn(name = "materiel_id")
    private Materiel materiel;

    public EvenementMateriel() {}

    public EvenementMateriel(Long id, int quantite, Evenement evenement, Materiel materiel) {
        this.id = id;
        this.quantite = quantite;
        this.evenement = evenement;
        this.materiel = materiel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) { this.materiel = materiel; }
}