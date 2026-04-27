package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;

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

    public List<UserAbonnement> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(List<UserAbonnement> abonnements) {
        this.abonnements = abonnements;
    }
}