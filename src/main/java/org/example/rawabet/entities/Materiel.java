package org.example.rawabet.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.rawabet.enums.MaterielStatus;
import java.util.List;

@Entity
public class Materiel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private String reference;
    private int quantiteDisponible;
    private double prixUnitaire;
    private boolean disponible;

    @Enumerated(EnumType.STRING)
    private MaterielStatus status;    // ✅ ACTIVE, MAINTENANCE, DAMAGED

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private CategorieMateriel categorie;

    @OneToMany(mappedBy = "materiel")
    @JsonIgnore
    private List<EvenementMateriel> evenementMateriels;

    @OneToMany(mappedBy = "materiel")
    @JsonIgnore
    private List<ReservationMateriel> reservations;

    public Materiel() {}

    public Materiel(Long id, String nom, String description, String reference,
                    int quantiteDisponible, double prixUnitaire, boolean disponible,
                    MaterielStatus status, CategorieMateriel categorie,
                    List<EvenementMateriel> evenementMateriels,
                    List<ReservationMateriel> reservations) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.reference = reference;
        this.quantiteDisponible = quantiteDisponible;
        this.prixUnitaire = prixUnitaire;
        this.disponible = disponible;
        this.status = status;
        this.categorie = categorie;
        this.evenementMateriels = evenementMateriels;
        this.reservations = reservations;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public int getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(int quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public MaterielStatus getStatus() { return status; }
    public void setStatus(MaterielStatus status) { this.status = status; }

    public CategorieMateriel getCategorie() { return categorie; }
    public void setCategorie(CategorieMateriel categorie) { this.categorie = categorie; }

    public List<EvenementMateriel> getEvenementMateriels() { return evenementMateriels; }
    public void setEvenementMateriels(List<EvenementMateriel> evenementMateriels) { this.evenementMateriels = evenementMateriels; }

    public List<ReservationMateriel> getReservations() { return reservations; }
    public void setReservations(List<ReservationMateriel> reservations) { this.reservations = reservations; }
}