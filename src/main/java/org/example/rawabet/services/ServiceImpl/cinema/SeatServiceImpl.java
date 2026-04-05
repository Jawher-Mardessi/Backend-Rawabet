package org.example.rawabet.services.ServiceImpl.cinema;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.cinema.request.ConfigureHallRequest;
import org.example.rawabet.dto.cinema.response.SeatResponse;
import org.example.rawabet.entities.cinema.SalleCinema;
import org.example.rawabet.entities.cinema.Seat;
import org.example.rawabet.entities.cinema.SeatRow;
import org.example.rawabet.enums.cinema.SeatType;
import org.example.rawabet.mappers.cinema.SeatMapper;
import org.example.rawabet.repositories.cinema.SalleCinemaRepository;
import org.example.rawabet.repositories.cinema.SeatRepository;
import org.example.rawabet.repositories.cinema.SeatRowRepository;
import org.example.rawabet.services.IService.cinema.ISeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class SeatServiceImpl implements ISeatService {

    private final SalleCinemaRepository salleRepository;

    private final SeatRowRepository seatRowRepository;

    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public void configureHall(ConfigureHallRequest request) {

        SalleCinema salle = salleRepository
                .findById(request.getSalleId())
                .orElseThrow(() ->
                        new RuntimeException("Salle not found")
                );

        List<SeatRow> rows = new ArrayList<>();

        for(int i = 0 ; i < request.getNumberOfRows() ; i++){

            char rowLetter = (char) ('A' + i);

            SeatRow row = new SeatRow();

            row.setRowLabel(String.valueOf(rowLetter));

            row.setSeatCount(request.getSeatsPerRow());

            row.setDisplayOrder(i + 1);

            row.setSalle(salle);

            SeatRow savedRow = seatRowRepository.save(row);

            generateSeats(savedRow , request.getSeatsPerRow());

            rows.add(savedRow);

        }

        int totalCapacity =
                request.getNumberOfRows() *
                        request.getSeatsPerRow();

        salle.setTotalCapacity(totalCapacity);

        salleRepository.save(salle);

    }

    private void generateSeats(SeatRow row , int seatsPerRow){

        List<Seat> seats = new ArrayList<>();

        for(int i = 1 ; i <= seatsPerRow ; i++){

            Seat seat = new Seat();

            String number =
                    (i < 10) ? "0"+i : String.valueOf(i);

            seat.setFullLabel(
                    row.getRowLabel() + "-" + number
            );

            seat.setSeatNumber(i);

            seat.setSeatType(SeatType.STANDARD);

            seat.setIsActive(true);

            seat.setRow(row);

            seats.add(seat);

        }

        seatRepository.saveAll(seats);

    }

    @Override
    public List<SeatResponse> getSeatsBySalle(Long salleId) {

        return seatRepository
                .findByRowSalleId(salleId)
                .stream()
                .map(SeatMapper::toResponse)
                .toList();

    }

    @Override
    public void disableSeat(Long seatId) {

        Seat seat = seatRepository
                .findById(seatId)
                .orElseThrow(() ->
                        new RuntimeException("Seat not found")
                );

        seat.setIsActive(false);

        seatRepository.save(seat);

    }

}