package org.example.rawabet.club.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.rawabet.club.enums.ClubJoinRequestStatus;

import java.time.LocalDateTime;

@Getter
@Builder

public class ClubJoinResponseDTO {

    private Long id;

    private Long userId;

    private String userName;

    private String motivation;

    private ClubJoinRequestStatus status;

    private LocalDateTime requestDate;

    private LocalDateTime processedDate;

}