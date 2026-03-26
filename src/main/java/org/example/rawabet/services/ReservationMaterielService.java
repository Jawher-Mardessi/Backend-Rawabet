package org.example.rawabet.services;

import org.example.rawabet.entities.ReservationMateriel;
import org.example.rawabet.repositories.ReservationMaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationMaterielService implements IReservationMaterielService {

    @Autowired
    private ReservationMaterielRepository reservationMaterielRepository;

    @Override
    public ReservationMateriel addReservation(ReservationMateriel rm) {
        return reservationMaterielRepository.save(rm);
    }

    @Override
    public ReservationMateriel updateReservation(ReservationMateriel rm) {
        return reservationMaterielRepository.save(rm);
    }

    @Override
    public void deleteReservation(Long id) {
        reservationMaterielRepository.deleteById(id);
    }

    @Override
    public ReservationMateriel getById(Long id) {
        Optional<ReservationMateriel> rm = reservationMaterielRepository.findById(id);
        return rm.orElse(null);
    }

    @Override
    public List<ReservationMateriel> getAll() {
        return reservationMaterielRepository.findAll();
    }
}
