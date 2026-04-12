package org.example.rawabet.club.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClubEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Club club;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private int maxPlaces;

    @Column(nullable = false)
    private int reservedPlaces;

    private String posterUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void init(){

        createdAt = LocalDateTime.now();

        if(reservedPlaces < 0){

            reservedPlaces = 0;

        }

    }

    @Transient
    public int getRemainingPlaces(){

        return maxPlaces - reservedPlaces;

    }

}