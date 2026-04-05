package org.example.rawabet.services.IService.evenement;

import org.example.rawabet.entities.SalleEvenement;

import java.util.List;

public interface ISalleEvenementService {

    SalleEvenement addSalle(SalleEvenement salle);

    SalleEvenement updateSalle(SalleEvenement salle);

    void deleteSalle(Long id);

    SalleEvenement getSalleById(Long id);

    List<SalleEvenement> getAllSalles();
}