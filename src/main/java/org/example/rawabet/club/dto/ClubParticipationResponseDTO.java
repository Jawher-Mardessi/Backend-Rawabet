package org.example.rawabet.club.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.rawabet.club.enums.ClubParticipationStatus;

import java.time.LocalDateTime;

@Getter
@Builder

public class ClubParticipationResponseDTO {

    private Long id;

    private Long eventId;

    private String eventTitle;

    private int reservedPlaces;

    private int remainingPlaces;

    private ClubParticipationStatus status;

    private LocalDateTime reservationDate;

}