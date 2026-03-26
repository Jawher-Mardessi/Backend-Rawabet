package org.example.rawabet.services;

import org.example.rawabet.entities.Abonnement;

import java.util.List;

public interface IAbonnementService {

    Abonnement addAbonnement(Abonnement abonnement);

    Abonnement updateAbonnement(Abonnement abonnement);

    void deleteAbonnement(Long id);

    Abonnement getAbonnementById(Long id);

    List<Abonnement> getAllAbonnements();
}