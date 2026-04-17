package org.example.rawabet.club.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ClubEventDetailDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private int maxPlaces;
    private int reservedPlaces;
    private int remainingPlaces;
    private String posterUrl;
    private LocalDateTime createdAt;

    /** null pour les non-admins, liste complète pour les admins */
    private List<ParticipantDTO> participants;

    @Getter
    @Builder
    public static class ParticipantDTO {
        private Long participationId;
        private Long userId;
        private String userName;
        private int reservedPlaces;
        private LocalDateTime reservationDate;
    }
}