package org.example.rawabet.services;

import org.example.rawabet.entities.Facture;

import java.util.List;

public interface IFactureService {

    Facture addFacture(Facture facture);

    Facture updateFacture(Facture facture);

    void deleteFacture(Long id);

    Facture getFactureById(Long id);

    List<Facture> getAllFactures();
}