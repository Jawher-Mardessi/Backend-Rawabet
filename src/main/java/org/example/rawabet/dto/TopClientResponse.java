package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.Level;

@Data
@Builder
public class TopClientResponse {
    private String nom;
    private String email;
    private int points;
    private Level level;
}