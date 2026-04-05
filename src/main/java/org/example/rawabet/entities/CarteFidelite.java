package org.example.rawabet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.Level;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteFidelite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int points = 0;

    @Column(nullable = false)
    private LocalDate dateExpiration;

    // 🔥 ENUM (PRO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    @JsonIgnore
    private User user;

    // 🔥 logique expiration
    public boolean isExpired() {
        return dateExpiration.isBefore(LocalDate.now());
    }
}