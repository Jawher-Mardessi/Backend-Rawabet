package org.example.rawabet.club.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.club.enums.ClubMemberStatus;
import org.example.rawabet.entities.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_club_member_user_club",
                        columnNames = {"user_id", "club_id"}
                )
        }
)
public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    private ClubMemberStatus status;

    private LocalDateTime joinedAt;

    // ✅ AJOUTÉS — pour la fonctionnalité Remove by admin
    @Column(length = 500)
    private String removeReason;   // optionnel — raison de l'expulsion

    private LocalDateTime removedAt; // date de l'expulsion
}