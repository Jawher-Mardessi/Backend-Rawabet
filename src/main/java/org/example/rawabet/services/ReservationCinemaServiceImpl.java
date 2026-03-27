package org.example.rawabet.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.*;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ReservationCinemaServiceImpl
        implements IReservationCinemaService {

    private final ReservationCinemaRepository reservationRepository;

    private final UserRepository userRepository;

    private final SeanceRepository seanceRepository;

    private final SeatRepository seatRepository;

    @Override
    @Transactional

    public ReservationCinema reserverAvecSeats(
            Long userId,
            Long seanceId,
            List<Long> seatIds){

        User user=userRepository.findById(userId)
                .orElseThrow(()->
                        new RuntimeException("User not found"));

        Seance seance=seanceRepository.findById(seanceId)
                .orElseThrow(()->
                        new RuntimeException("Seance not found"));

        ReservationCinema reservation=
                new ReservationCinema();

        reservation.setUser(user);

        reservation.setSeance(seance);

        reservation.setDateReservation(LocalDate.now());

        reservation.setStatut(
                ReservationStatus.PENDING);

        reservation=reservationRepository.save(reservation);

        List<Seat> seats=new ArrayList<>();

        for(Long seatId:seatIds){

            Seat seat=seatRepository
                    .findById(seatId)
                    .orElseThrow(()->
                            new RuntimeException(
                                    "Seat not found"));

            if(seat.getReservation()!=null){

                throw new RuntimeException(
                        "Seat "+seat.getNumero()
                                +" already reserved");

            }

            if(!seat.getSeance()
                    .getId()
                    .equals(seanceId)){

                throw new RuntimeException(
                        "Seat does not belong to this seance");

            }

            seat.setReservation(reservation);

            seatRepository.save(seat);

            seats.add(seat);

        }

        reservation.setSeats(seats);

        return reservation;

    }

    @Override

    public List<ReservationCinema>
    getAllReservations(){

        return reservationRepository.findAll();

    }

    @Override

    public ReservationCinema
    getReservationById(Long id){

        return reservationRepository
                .findById(id)
                .orElse(null);

    }

}