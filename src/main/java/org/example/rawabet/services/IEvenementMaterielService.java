package org.example.rawabet.services;

import org.example.rawabet.entities.EvenementMateriel;
import java.util.List;

public interface IEvenementMaterielService {
    // Assign a materiel to an event
    EvenementMateriel assignerMateriel(Long evenementId, Long materielId, int quantite);

    // Update quantity of an assigned materiel
    EvenementMateriel updateQuantite(Long evenementMaterielId, int nouvelleQuantite);

    // Remove a materiel from an event
    void retirerMateriel(Long evenementMaterielId);

    // Get all materiel assignments for an event
    List<EvenementMateriel> getByEvenement(Long evenementId);

    // Get all event assignments for a materiel
    List<EvenementMateriel> getByMateriel(Long materielId);
}