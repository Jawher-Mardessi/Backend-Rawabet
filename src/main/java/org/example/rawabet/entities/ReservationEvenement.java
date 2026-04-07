package org.example.rawabet.entities;

import jakarta.persistence.*;
import org.example.rawabet.enums.ReservationStatus;
import java.time.LocalDateTime;

@Entity
public class ReservationEvenement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateReservation;
    private LocalDateTime dateExpiration;   // ✅ auto-cancel if pending after this

    @Enumerated(EnumType.STRING)
    private ReservationStatus statut;

    private boolean enAttente;             // ✅ true = on waitlist

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @OneToOne
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;

    public ReservationEvenement() {}

    public ReservationEvenement(Long id, LocalDateTime dateReservation,
                                LocalDateTime dateExpiration, ReservationStatus statut,
                                boolean enAttente, User user, Evenement evenement,
                                Paiement paiement) {
        this.id = id;
        this.dateReservation = dateReservation;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
        this.enAttente = enAttente;
        this.user = user;
        this.evenement = evenement;
        this.paiement = paiement;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }

    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }

    public ReservationStatus getStatut() { return statut; }
    public void setStatut(ReservationStatus statut) { this.statut = statut; }

    public boolean isEnAttente() { return enAttente; }
    public void setEnAttente(boolean enAttente) { this.enAttente = enAttente; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    public Paiement getPaiement() { return paiement; }
    public void setPaiement(Paiement paiement) { this.paiement = paiement; }
}