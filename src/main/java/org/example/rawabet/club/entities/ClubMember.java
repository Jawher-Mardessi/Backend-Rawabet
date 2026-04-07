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

public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    private ClubMemberStatus status;

    private LocalDateTime joinedAt;

}