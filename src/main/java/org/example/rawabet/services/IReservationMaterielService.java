package org.example.rawabet.services;

import org.example.rawabet.entities.ReservationMateriel;
import org.example.rawabet.enums.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface IReservationMaterielService {
    ReservationMateriel addReservation(ReservationMateriel rm);
    ReservationMateriel updateReservation(ReservationMateriel rm);
    void deleteReservation(Long id);
    ReservationMateriel getById(Long id);
    List<ReservationMateriel> getAll();
    ReservationMateriel reserverMateriel(Long userId, Long materielId, int quantite,
                                         LocalDateTime dateDebut, LocalDateTime dateFin);
    ReservationMateriel annulerReservation(Long reservationId);
    ReservationMateriel confirmerReservation(Long reservationId);
    List<ReservationMateriel> getReservationsByUser(Long userId);
    List<ReservationMateriel> getReservationsByMateriel(Long materielId);
    List<ReservationMateriel> getReservationsByStatus(ReservationStatus statut);
    List<ReservationMateriel> getOverlappingReservations(Long materielId,
                                                         LocalDateTime dateDebut,
                                                         LocalDateTime dateFin);
    ReservationMateriel extendReservation(Long reservationId,
                                          LocalDateTime nouvelleDataFin);  // ✅ new
    ReservationMateriel retourPartiel(Long reservationId,
                                      int quantiteRetournee);              // ✅ new
}