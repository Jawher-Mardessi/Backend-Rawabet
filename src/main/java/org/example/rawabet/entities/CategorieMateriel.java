package org.example.rawabet.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
public class CategorieMateriel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;

    @OneToMany(mappedBy = "categorie")
    @JsonIgnore
    private List<Materiel> materiels;

    public CategorieMateriel() {}

    public CategorieMateriel(Long id, String nom, String description, List<Materiel> materiels) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.materiels = materiels;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Materiel> getMateriels() { return materiels; }
    public void setMateriels(List<Materiel> materiels) { this.materiels = materiels; }
}