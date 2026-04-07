package org.example.rawabet.entities;

import jakarta.persistence.*;
import org.example.rawabet.enums.ReservationStatus;
import java.time.LocalDateTime;

@Entity
public class ReservationMateriel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantite;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private ReservationStatus statut;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "materiel_id")
    private Materiel materiel;

    public ReservationMateriel() {}

    public ReservationMateriel(Long id, int quantite, LocalDateTime dateDebut,
                               LocalDateTime dateFin, ReservationStatus statut,
                               User user, Materiel materiel) {
        this.id = id;
        this.quantite = quantite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.user = user;
        this.materiel = materiel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public ReservationStatus getStatut() { return statut; }
    public void setStatut(ReservationStatus statut) { this.statut = statut; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) { this.materiel = materiel; }
}