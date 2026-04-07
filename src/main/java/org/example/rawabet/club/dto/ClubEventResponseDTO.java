package org.example.rawabet.club.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder

public class ClubEventResponseDTO {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime eventDate;

    private int maxPlaces;

    private int reservedPlaces;

    private int remainingPlaces;

    private String posterUrl;

    private LocalDateTime createdAt;

}