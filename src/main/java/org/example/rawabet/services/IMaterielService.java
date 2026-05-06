package org.example.rawabet.services;

import org.example.rawabet.dto.MaterielOccupationDTO;
import org.example.rawabet.entities.Materiel;
import org.example.rawabet.enums.MaterielStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface IMaterielService {
    Materiel addMateriel(Materiel materiel);
    Materiel updateMateriel(Materiel materiel);
    void deleteMateriel(Long id);
    Materiel getMaterielById(Long id);
    List<Materiel> getAllMateriels();
    boolean isMaterielAvailable(Long materielId, int quantiteDemandee,
                                LocalDateTime dateDebut, LocalDateTime dateFin);
    int getAvailableQuantity(Long materielId, LocalDateTime dateDebut, LocalDateTime dateFin);
    List<Materiel> getAvailableMateriels(LocalDateTime dateDebut, LocalDateTime dateFin);
    List<Materiel> getMaterielsByCategorie(Long categorieId);
    Materiel updateStatus(Long id, MaterielStatus status);
    List<Materiel> getMaterielsByStatus(MaterielStatus status);
    List<MaterielOccupationDTO> getFullOccupation(Long materielId);
}