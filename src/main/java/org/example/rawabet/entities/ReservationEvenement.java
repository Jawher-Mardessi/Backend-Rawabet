package org.example.rawabet.entities;

import jakarta.persistence.*;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.enums.ReservationEvenementAttribut;
import java.time.LocalDateTime;

@Entity
public class ReservationEvenement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateReservation;
    private LocalDateTime dateExpiration;   // ✅ auto-cancel if pending after this
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ReservationStatus statut;

    @Enumerated(EnumType.STRING)
    private ReservationEvenementAttribut attribut;

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
                                ReservationEvenementAttribut attribut,
                                String phoneNumber, boolean enAttente, User user, Evenement evenement,
                                Paiement paiement) {
        this.id = id;
        this.dateReservation = dateReservation;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
        this.attribut = attribut;
        this.phoneNumber = phoneNumber;
        this.enAttente = enAttente;
        this.user = user;
        this.evenement = evenement;
        this.paiement = paiement;
    }

    @PrePersist
    protected void onCreate() {
        if (attribut == null) {
            attribut = ReservationEvenementAttribut.CONFIRMED;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }

    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }

    public ReservationStatus getStatut() { return statut; }
    public void setStatut(ReservationStatus statut) { this.statut = statut; }

    public ReservationEvenementAttribut getAttribut() { return attribut; }
    public void setAttribut(ReservationEvenementAttribut attribut) { this.attribut = attribut; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isEnAttente() { return enAttente; }
    public void setEnAttente(boolean enAttente) { this.enAttente = enAttente; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    public Paiement getPaiement() { return paiement; }
    public void setPaiement(Paiement paiement) { this.paiement = paiement; }
}