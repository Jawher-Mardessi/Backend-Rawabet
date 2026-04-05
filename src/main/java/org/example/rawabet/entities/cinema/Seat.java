package org.example.rawabet.entities.cinema;

import org.example.rawabet.enums.cinema.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"row_id","fullLabel"}
                )
        }
)

@Getter
@Setter
@Builder

@NoArgsConstructor
@AllArgsConstructor

public class Seat extends BaseEntity {

    @Column(nullable = false)
    private String fullLabel;

    @Column(nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "row_id", nullable = false)
    private SeatRow row;

}