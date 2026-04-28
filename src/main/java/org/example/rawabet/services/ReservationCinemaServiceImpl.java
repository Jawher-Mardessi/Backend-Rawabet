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
import java.util.stream.Collectors;

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
    public List<ReservationCinemaResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationCinemaResponse reserver(CreateReservationCinemaRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Seance seance = seanceRepository.findById(request.getSeanceId())
                .orElseThrow(() -> new RuntimeException("Seance not found"));

        if (seance.getSalleCinema() == null) {
            throw new RuntimeException("Salle not found for seance");
        }

        Seat seat = seatRepository.findBySeatNumberAndRowSalleIdAndIsActiveTrue(
                        request.getSeatNumero(),
                        seance.getSalleCinema().getId()
                )
                .orElseThrow(() -> new RuntimeException("Seat not found for numero: " + request.getSeatNumero()));

        boolean alreadyReserved = reservationRepository
                .existsBySeanceIdAndSeatId(request.getSeanceId(), seat.getId());

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

        return mapToResponse(reservation);
    }

    private ReservationCinemaResponse mapToResponse(ReservationCinema reservation) {
        return ReservationCinemaResponse.builder()
                .id(reservation.getId())
                .dateReservation(reservation.getDateReservation() != null ?
                        reservation.getDateReservation().toString() : LocalDate.now().toString())
                .statut(reservation.getStatut() != null ?
                        reservation.getStatut().name() : "PENDING")
                .userId(reservation.getUser() != null ?
                        reservation.getUser().getId() : null)
                .seanceId(reservation.getSeance() != null ?
                        reservation.getSeance().getId() : null)
                .seatId(reservation.getSeat() != null ?
                        reservation.getSeat().getId() : null)
                .build();
    }
}
