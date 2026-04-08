package org.example.rawabet.services;

import org.example.rawabet.cinema.entities.Seat;
import org.example.rawabet.cinema.repositories.SeatRepository;
import org.example.rawabet.dto.reservationCinema.request.CreateReservationCinemaRequest;
import org.example.rawabet.dto.reservationCinema.response.ReservationCinemaResponse;
import org.example.rawabet.entities.ReservationCinema;
import org.example.rawabet.entities.Seance;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.repositories.ReservationCinemaRepository;
import org.example.rawabet.repositories.SeanceRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationCinemaServiceImpl implements IReservationCinemaService {

    @Autowired
    private ReservationCinemaRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public ReservationCinema addReservation(ReservationCinema reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public ReservationCinema updateReservation(ReservationCinema reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public ReservationCinema getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    @Override
    public List<ReservationCinema> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public ReservationCinemaResponse reserver(CreateReservationCinemaRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Seance seance = seanceRepository.findById(request.getSeanceId())
                .orElseThrow(() -> new RuntimeException("Seance not found"));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        boolean alreadyReserved = reservationRepository
                .existsBySeanceIdAndSeatId(request.getSeanceId(), request.getSeatId());

        if (alreadyReserved) {
            throw new RuntimeException("Seat already reserved for this seance");
        }

        ReservationCinema reservation = new ReservationCinema();
        reservation.setUser(user);
        reservation.setSeance(seance);
        reservation.setSeat(seat);
        reservation.setDateReservation(LocalDate.now());
        reservation.setStatut(ReservationStatus.PENDING);

        reservation = reservationRepository.save(reservation);

        return ReservationCinemaResponse.builder()
                .id(reservation.getId())
                .dateReservation(reservation.getDateReservation().toString())
                .statut(reservation.getStatut().name())
                .userId(user.getId())
                .seanceId(seance.getId())
                .seatId(seat.getId())
                .build();
    }
}