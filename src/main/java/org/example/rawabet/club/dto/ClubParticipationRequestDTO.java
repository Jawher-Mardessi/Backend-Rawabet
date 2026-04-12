package org.example.rawabet.club.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClubParticipationRequestDTO {

    @NotNull
    private Long eventId;

    @Min(1)
    private int places;

}