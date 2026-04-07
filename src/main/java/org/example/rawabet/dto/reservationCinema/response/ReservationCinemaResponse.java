package org.example.rawabet.dto.reservationCinema.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationCinemaResponse {
    private Long id;
    private String dateReservation;
    private String statut;
    private Long userId;
    private Long seanceId;
    private Long seatId;
}