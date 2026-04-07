package org.example.rawabet.club.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class ClubEventRequestDTO {

    @NotBlank
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull
    @Future
    private LocalDateTime eventDate;

    @Min(1)
    private int maxPlaces;

    private String posterUrl;

}