package org.example.rawabet.cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.cinema.enums.SeatType;

@Data
@Builder
public class SeatResponse {

    private Long id;

    private String fullLabel;

    private Integer seatNumber;

    private SeatType seatType;

    private Boolean isActive;

}