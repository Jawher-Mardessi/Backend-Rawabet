package org.example.rawabet.club.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.rawabet.club.enums.ClubMemberStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClubMemberResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private ClubMemberStatus status;
    private LocalDateTime joinedAt;

    // ✅ AJOUTÉS
    private String removeReason;
    private LocalDateTime removedAt;
}