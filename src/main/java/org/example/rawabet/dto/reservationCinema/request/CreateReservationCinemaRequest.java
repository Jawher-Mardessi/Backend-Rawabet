package org.example.rawabet.dto.reservationCinema.request;

import lombok.Data;

@Data
public class CreateReservationCinemaRequest {
    private Long userId;
    private Long seanceId;
    private Integer seatNumero;
}
