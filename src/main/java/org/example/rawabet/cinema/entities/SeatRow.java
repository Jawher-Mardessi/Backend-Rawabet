package org.example.rawabet.cinema.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "seat_rows",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seatrow_salle_label",
                        columnNames = {"salle_id", "rowLabel"}
                )
        }
)

@Getter
@Setter
@Builder

@NoArgsConstructor
@AllArgsConstructor

public class SeatRow extends BaseEntity {

    @Column(nullable = false)
    private String rowLabel;

    @Column(nullable = false)
    private Integer seatCount;

    @Column(nullable = false)
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id", nullable = false)
    private SalleCinema salle;

    @OneToMany(
            mappedBy = "row",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Seat> seats;

}