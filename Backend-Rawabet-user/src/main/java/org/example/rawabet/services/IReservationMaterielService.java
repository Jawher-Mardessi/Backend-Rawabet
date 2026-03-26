package org.example.rawabet.services;

import org.example.rawabet.entities.ReservationMateriel;

import java.util.List;

public interface IReservationMaterielService {

    ReservationMateriel addReservation(ReservationMateriel rm);

    ReservationMateriel updateReservation(ReservationMateriel rm);

    void deleteReservation(Long id);

    ReservationMateriel getById(Long id);

    List<ReservationMateriel> getAll();
}