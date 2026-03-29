package org.example.rawabet.services;

import org.example.rawabet.entities.Seat;
import org.example.rawabet.repositories.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class SeatServiceImpl implements ISeatService {

    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public Seat addSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Override
    public Seat updateSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Override
    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    @Override
    public Seat getSeatById(Long id) {
        return seatRepository.findById(id).orElse(null);
    }

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

}