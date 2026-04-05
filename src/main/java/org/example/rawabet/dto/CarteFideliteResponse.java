package org.example.rawabet.dto;

import lombok.*;
import org.example.rawabet.enums.Level;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarteFideliteResponse {

    private int points;
    private LocalDate dateExpiration;
    private Level level;
}