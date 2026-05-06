package org.example.rawabet.cinema.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.cinema.enums.HallType;
import org.example.rawabet.cinema.enums.ScreenType;


import java.util.List;

@Entity
@Table(name = "salles_cinema")

@Getter
@Setter
@Builder

@NoArgsConstructor
@AllArgsConstructor

public class SalleCinema extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HallType hallType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScreenType screenType;

    @Builder.Default
    private Integer totalCapacity = 0;

    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    @JsonIgnoreProperties("salles")  // ← AJOUTE CETTE LIGNE

    private Cinema cinema;

    @OneToMany(
            mappedBy = "salle",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<SeatRow> rows;

}