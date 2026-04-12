package org.example.rawabet.cinema.mappers;

import org.example.rawabet.cinema.dto.response.SeatResponse;
import org.example.rawabet.cinema.entities.Seat;

public class SeatMapper {

    public static SeatResponse toResponse(Seat seat) {

        return SeatResponse.builder()
                .id(seat.getId())
                .fullLabel(seat.getFullLabel())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .isActive(seat.getIsActive())
                .build();

    }

}