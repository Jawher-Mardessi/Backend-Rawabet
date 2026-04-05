package org.example.rawabet.services.IService.evenement;

import org.example.rawabet.entities.Evenement;

import java.util.List;

public interface IEvenementService {

    Evenement addEvenement(Evenement evenement);

    Evenement updateEvenement(Evenement evenement);

    void deleteEvenement(Long id);

    Evenement getEvenementById(Long id);

    List<Evenement> getAllEvenements();
}