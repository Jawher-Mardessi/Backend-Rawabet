package org.example.rawabet.cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.cinema.enums.SeatType;

@Data
@Builder
public class SeatRowResponse {
    private Long id;
    private String rowLabel;
    private Integer seatCount;
    private Integer displayOrder;
    private SeatType dominantSeatType; // type majoritaire de la rangée
}