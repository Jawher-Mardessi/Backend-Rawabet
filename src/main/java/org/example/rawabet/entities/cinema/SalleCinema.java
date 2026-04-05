package org.example.rawabet.entities.cinema;

import org.example.rawabet.enums.cinema.HallType;
import org.example.rawabet.enums.cinema.ScreenType;
import jakarta.persistence.*;
import lombok.*;

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
    private Cinema cinema;

    @OneToMany(
            mappedBy = "salle",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<SeatRow> rows;

}