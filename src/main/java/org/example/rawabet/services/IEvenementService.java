package org.example.rawabet.services;

import org.example.rawabet.entities.Evenement;
import org.example.rawabet.entities.EvenementMateriel;
import org.example.rawabet.enums.EvenementStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface IEvenementService {
    Evenement addEvenement(Evenement evenement);
    Evenement updateEvenement(Evenement evenement);
    void deleteEvenement(Long id);
    Evenement getEvenementById(Long id);
    List<Evenement> getAllEvenements();
    Evenement assignSalleToEvenement(Long evenementId, Long salleId);
    EvenementMateriel assignMaterielToEvenement(Long evenementId, Long materielId, int quantite);
    void removeMaterielFromEvenement(Long evenementMaterielId);
    List<EvenementMateriel> getMaterielsByEvenement(Long evenementId);
    boolean hasAvailablePlaces(Long evenementId);
    int getRemainingPlaces(Long evenementId);
    List<Evenement> getUpcomingEvenements();
    List<Evenement> getEvenementsBySalle(Long salleId);
    List<Evenement> getEvenementsByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin);
    Evenement updateStatus(Long id, EvenementStatus status);          // ✅ new
    List<Evenement> searchByKeyword(String keyword);                  // ✅ new
    List<Evenement> getEvenementsByStatus(EvenementStatus status);    // ✅ new
    int getWaitlistCount(Long evenementId);                           // ✅ new
}