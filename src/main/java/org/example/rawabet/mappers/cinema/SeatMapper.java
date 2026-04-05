package org.example.rawabet.mappers.cinema;

import org.example.rawabet.dto.cinema.response.SeatResponse;
import org.example.rawabet.entities.cinema.Seat;

public class SeatMapper {

    public static SeatResponse toResponse(Seat seat){

        return SeatResponse.builder()
                .id(seat.getId())
                .fullLabel(seat.getFullLabel())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .rowLabel(seat.getRow().getRowLabel())
                .build();

    }

}
