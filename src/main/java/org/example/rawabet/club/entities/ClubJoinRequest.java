package org.example.rawabet.club.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.club.enums.ClubJoinRequestStatus;
import org.example.rawabet.entities.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClubJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(length = 1000)
    private String motivation;

    @Enumerated(EnumType.STRING)
    private ClubJoinRequestStatus status;

    private LocalDateTime requestDate;

    private LocalDateTime processedDate;

}