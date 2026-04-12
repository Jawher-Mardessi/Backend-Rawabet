package org.example.rawabet.club.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.club.enums.ClubParticipationStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"club_member_id","club_event_id"})
        },
        indexes = {
                @Index(columnList = "club_member_id"),
                @Index(columnList = "club_event_id")
        }
)

public class ClubParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ClubMember clubMember;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ClubEvent clubEvent;

    @Column(nullable = false)
    private int reservedPlaces;

    @Enumerated(EnumType.STRING)
    private ClubParticipationStatus status;

    private LocalDateTime reservationDate;

}