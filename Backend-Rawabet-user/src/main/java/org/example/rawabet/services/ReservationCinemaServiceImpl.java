package org.example.rawabet.services;

import org.example.rawabet.entities.ReservationCinema;
import org.example.rawabet.entities.Seance;
import org.example.rawabet.entities.Ticket;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.enums.TicketStatus;
import org.example.rawabet.repositories.ReservationCinemaRepository;
import org.example.rawabet.repositories.SeanceRepository;
import org.example.rawabet.repositories.TicketRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.IReservationCinemaService;
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
    private TicketRepository ticketRepository;

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
    public ReservationCinema reserverAvecTickets(Long userId, Long seanceId, int nbTickets) {

        // 🔴 1. récupérer user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔴 2. récupérer séance
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Seance not found"));

        // 🔴 3. créer réservation
        ReservationCinema reservation = new ReservationCinema();
        reservation.setUser(user);
        reservation.setSeance(seance);
        reservation.setDateReservation(LocalDate.now());
        reservation.setStatut(ReservationStatus.PENDING);

        reservation = reservationRepository.save(reservation);

        // 🔥 4. créer tickets automatiquement
        for (int i = 0; i < nbTickets; i++) {
            Ticket ticket = new Ticket();
            ticket.setStatut(TicketStatus.RESERVED);
            ticket.setReservationCinema(reservation);
            ticketRepository.save(ticket);
        }

        return reservation;
    }
}