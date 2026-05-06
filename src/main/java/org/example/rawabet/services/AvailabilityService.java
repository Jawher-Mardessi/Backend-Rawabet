package org.example.rawabet.services;

import org.example.rawabet.entities.EvenementMateriel;
import org.example.rawabet.entities.Materiel;
import org.example.rawabet.entities.ReservationMateriel;
import org.example.rawabet.repositories.EvenementMaterielRepository;
import org.example.rawabet.repositories.MaterielRepository;
import org.example.rawabet.repositories.ReservationMaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvailabilityService implements IAvailabilityService {

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private ReservationMaterielRepository reservationMaterielRepository;

    @Autowired
    private EvenementMaterielRepository evenementMaterielRepository;

    @Override
    public int getAvailableQuantity(Long materielId, LocalDateTime startDate, LocalDateTime endDate) {
        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new RuntimeException("Matériel introuvable avec l'id: " + materielId));

        int totalQuantity = materiel.getQuantiteTotale();

        // Find all standalone reservations that overlap with the given time frame
        List<ReservationMateriel> overlappingReservations = reservationMaterielRepository.findOverlappingReservations(materielId, startDate, endDate);
        int reservedQuantity = overlappingReservations.stream()
                .mapToInt(ReservationMateriel::getQuantiteReservee)
                .sum();

        // Find all event material allocations that overlap with the given time frame
        List<EvenementMateriel> overlappingEventMateriels = evenementMaterielRepository.findOverlappingEventMateriels(materielId, startDate, endDate);
        int eventAllocatedQuantity = overlappingEventMateriels.stream()
                .mapToInt(EvenementMateriel::getQuantite)
                .sum();

        return totalQuantity - reservedQuantity - eventAllocatedQuantity;
    }
}
