package org.example.rawabet.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.rawabet.enums.SalleStatus;
import org.example.rawabet.enums.SalleType;
import java.util.List;

@Entity
public class SalleEvenement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private int capacite;

    @Enumerated(EnumType.STRING)
    private SalleType type;

    @Enumerated(EnumType.STRING)
    private SalleStatus status;   // ✅ ACTIVE or MAINTENANCE

    @OneToMany(mappedBy = "salle")
    @JsonIgnore
    private List<Evenement> evenements;

    public SalleEvenement() {}

    public SalleEvenement(Long id, String nom, int capacite, SalleType type,
                          SalleStatus status, List<Evenement> evenements) {
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.type = type;
        this.status = status;
        this.evenements = evenements;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public SalleType getType() { return type; }
    public void setType(SalleType type) { this.type = type; }

    public SalleStatus getStatus() { return status; }
    public void setStatus(SalleStatus status) { this.status = status; }

    public List<Evenement> getEvenements() { return evenements; }
    public void setEvenements(List<Evenement> evenements) { this.evenements = evenements; }
}