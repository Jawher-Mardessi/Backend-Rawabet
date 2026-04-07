package org.example.rawabet.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.rawabet.enums.EvenementStatus;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Evenement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int nombreDePlaces;

    @Enumerated(EnumType.STRING)
    private EvenementStatus status;   // ✅ DRAFT, PUBLISHED, CANCELLED

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private SalleEvenement salle;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EvenementMateriel> materiels;

    @OneToMany(mappedBy = "evenement")
    @JsonIgnore
    private List<ReservationEvenement> reservations;

    public Evenement() {}

    public Evenement(Long id, String titre, String description, LocalDateTime dateDebut,
                     LocalDateTime dateFin, int nombreDePlaces, EvenementStatus status,
                     SalleEvenement salle, List<EvenementMateriel> materiels,
                     List<ReservationEvenement> reservations) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nombreDePlaces = nombreDePlaces;
        this.status = status;
        this.salle = salle;
        this.materiels = materiels;
        this.reservations = reservations;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public int getNombreDePlaces() { return nombreDePlaces; }
    public void setNombreDePlaces(int nombreDePlaces) { this.nombreDePlaces = nombreDePlaces; }

    public EvenementStatus getStatus() { return status; }
    public void setStatus(EvenementStatus status) { this.status = status; }

    public SalleEvenement getSalle() { return salle; }
    public void setSalle(SalleEvenement salle) { this.salle = salle; }

    public List<EvenementMateriel> getMateriels() { return materiels; }
    public void setMateriels(List<EvenementMateriel> materiels) { this.materiels = materiels; }

    public List<ReservationEvenement> getReservations() { return reservations; }
    public void setReservations(List<ReservationEvenement> reservations) { this.reservations = reservations; }
}