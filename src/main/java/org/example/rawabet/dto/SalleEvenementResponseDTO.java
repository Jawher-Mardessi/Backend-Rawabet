package org.example.rawabet.dto;

import org.example.rawabet.enums.SalleStatus;
import org.example.rawabet.enums.SalleType;

public class SalleEvenementResponseDTO {

    private Long id;
    private String nom;
    private int capacite;
    private SalleType type;
    private SalleStatus status;

    public SalleEvenementResponseDTO() {}

    public SalleEvenementResponseDTO(Long id, String nom, int capacite,
                                     SalleType type, SalleStatus status) {
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.type = type;
        this.status = status;
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
}