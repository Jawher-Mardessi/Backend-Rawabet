package org.example.rawabet.club.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClubJoinRequestDTO {

    @NotBlank(message = "Motivation is required")
    @Size(max = 1000, message = "Motivation too long")
    private String motivation;

}