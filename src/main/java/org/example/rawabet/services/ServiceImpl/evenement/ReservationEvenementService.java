package org.example.rawabet.services.ServiceImpl.evenement;

import org.example.rawabet.entities.ReservationEvenement;
import org.example.rawabet.repositories.ReservationEvenementRepository;
import org.example.rawabet.services.IService.evenement.IReservationEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationEvenementService implements IReservationEvenementService {

    @Autowired
    private ReservationEvenementRepository reservationEvenementRepository;

    @Override
    public ReservationEvenement addReservation(ReservationEvenement reservation) {
        return reservationEvenementRepository.save(reservation);
    }

    @Override
    public ReservationEvenement updateReservation(ReservationEvenement reservation) {
        return reservationEvenementRepository.save(reservation);
    }

    @Override
    public void deleteReservation(Long id) {
        reservationEvenementRepository.deleteById(id);
    }

    @Override
    public ReservationEvenement getReservationById(Long id) {
        Optional<ReservationEvenement> reservation = reservationEvenementRepository.findById(id);
        return reservation.orElse(null);
    }

    @Override
    public List<ReservationEvenement> getAllReservations() {
        return reservationEvenementRepository.findAll();
    }
}
