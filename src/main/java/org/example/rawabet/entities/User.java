package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(unique = true)
    private String email;

    private String avatarUrl;

    @JsonIgnore
    private String password;

    @Column(name = "token_version", nullable = false)
    private int tokenVersion = 0;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;

    // ── Ban temporaire ─────────────────────────────────────────────────
    // null = pas de ban en cours ou ban permanent (isActive = false, banUntil = null)
    // non-null = ban temporaire → automatiquement levé par le scheduler
    @Column(name = "ban_until")
    private LocalDateTime banUntil;

    @Column(name = "ban_reason", length = 500)
    private String banReason;

    // ── Tentatives de connexion échouées ────────────────────────────────
    @Column(name = "login_failed_attempts", nullable = false, columnDefinition = "int default 0")
    private int loginFailedAttempts = 0;

    @Column(name = "login_locked_until")
    private LocalDateTime loginLockedUntil;

    // ── Audit temporel ─────────────────────────────────────────────────
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ReservationCinema> reservationCinemas;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ReservationEvenement> reservationEvenements;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Feedback> feedbacks;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private CarteFidelite carteFidelite;


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserAbonnement> abonnements;

    // ── Helpers métier ─────────────────────────────────────────────────

    /**
     * Retourne true si le compte est actuellement bloqué (ban temporaire expiré = OK).
     */
    public boolean isCurrentlyBanned() {
        if (!isActive) {
            // Ban permanent OU ban temporaire pas encore expiré
            if (banUntil == null) return true;           // ban permanent
            if (LocalDateTime.now().isBefore(banUntil)) return true; // ban temporaire actif
            // Ban temporaire expiré → réactiver automatiquement
            return false;
        }
        return false;
    }

    /**
     * Retourne true si le compte est verrouillé suite à trop de tentatives.
     */
    public boolean isLoginLocked() {
        return loginLockedUntil != null && LocalDateTime.now().isBefore(loginLockedUntil);
    }

    public List<UserAbonnement> getAbonnements() {
        return abonnements;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<ReservationCinema> getReservationCinemas() {
        return reservationCinemas;
    }

    public void setReservationCinemas(List<ReservationCinema> reservationCinemas) {
        this.reservationCinemas = reservationCinemas;
    }

    public List<ReservationEvenement> getReservationEvenements() {
        return reservationEvenements;
    }

    public void setReservationEvenements(List<ReservationEvenement> reservationEvenements) {
        this.reservationEvenements = reservationEvenements;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public CarteFidelite getCarteFidelite() {
        return carteFidelite;
    }

    public void setCarteFidelite(CarteFidelite carteFidelite) {
        this.carteFidelite = carteFidelite;
    }



    public void setAbonnements(List<UserAbonnement> abonnements) {
        this.abonnements = abonnements;
    }
}