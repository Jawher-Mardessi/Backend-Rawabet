package org.example.rawabet.entities;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    @Column(unique = true)
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;  // ✅ Set au lieu de List

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

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ChatInstantane> messages;

    @OneToOne(mappedBy = "user")
    private CarteFidelite carteFidelite;

    @OneToOne(mappedBy = "user")
    private Abonnement abonnement;

    @ManyToMany
    @JsonIgnore
    private List<ClubCinema> clubs;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public List<ReservationCinema> getReservationCinemas() { return reservationCinemas; }
    public void setReservationCinemas(List<ReservationCinema> reservationCinemas) { this.reservationCinemas = reservationCinemas; }
    public List<ReservationEvenement> getReservationEvenements() { return reservationEvenements; }
    public void setReservationEvenements(List<ReservationEvenement> reservationEvenements) { this.reservationEvenements = reservationEvenements; }
    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }
    public List<Feedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }
    public List<ChatInstantane> getMessages() { return messages; }
    public void setMessages(List<ChatInstantane> messages) { this.messages = messages; }
    public CarteFidelite getCarteFidelite() { return carteFidelite; }
    public void setCarteFidelite(CarteFidelite carteFidelite) { this.carteFidelite = carteFidelite; }
    public Abonnement getAbonnement() { return abonnement; }
    public void setAbonnement(Abonnement abonnement) { this.abonnement = abonnement; }
    public List<ClubCinema> getClubs() { return clubs; }
    public void setClubs(List<ClubCinema> clubs) { this.clubs = clubs; }
}