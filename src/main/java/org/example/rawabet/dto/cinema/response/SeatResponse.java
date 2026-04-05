package org.example.rawabet.dto.cinema.response;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.cinema.SeatType;

@Data
@Builder

public class SeatResponse {

    private Long id;

    private String fullLabel;

    private Integer seatNumber;

    private SeatType seatType;

    private String rowLabel;

}
