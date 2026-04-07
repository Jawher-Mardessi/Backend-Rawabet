package org.example.rawabet.services;

import org.example.rawabet.entities.SalleEvenement;
import org.example.rawabet.enums.SalleStatus;
import org.example.rawabet.enums.SalleType;
import java.time.LocalDateTime;
import java.util.List;

public interface ISalleEvenementService {
    SalleEvenement addSalle(SalleEvenement salle);
    SalleEvenement updateSalle(SalleEvenement salle);
    void deleteSalle(Long id);
    SalleEvenement getSalleById(Long id);
    List<SalleEvenement> getAllSalles();
    boolean isSalleAvailable(Long salleId, LocalDateTime dateDebut, LocalDateTime dateFin);
    List<SalleEvenement> getAvailableSalles(LocalDateTime dateDebut, LocalDateTime dateFin);
    List<SalleEvenement> getSallesByType(SalleType type);             // ✅ new
    SalleEvenement updateStatus(Long id, SalleStatus status);         // ✅ new
}