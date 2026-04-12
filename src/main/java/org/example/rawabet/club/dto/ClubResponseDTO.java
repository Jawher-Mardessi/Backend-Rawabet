package org.example.rawabet.club.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClubResponseDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}