package org.example.rawabet.services;

import org.example.rawabet.entities.Evenement;
import org.example.rawabet.entities.EvenementMateriel;
import org.example.rawabet.entities.Materiel;
import org.example.rawabet.enums.MaterielStatus;
import org.example.rawabet.repositories.EvenementMaterielRepository;
import org.example.rawabet.repositories.EvenementRepository;
import org.example.rawabet.repositories.MaterielRepository;
import org.example.rawabet.services.IEvenementMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EvenementMaterielService implements IEvenementMaterielService {

    @Autowired
    private EvenementMaterielRepository evenementMaterielRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private IAvailabilityService availabilityService;

    @Override
    public EvenementMateriel assignerMateriel(Long evenementId, Long materielId, int quantite) {
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement introuvable avec l'id: " + evenementId));

        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new RuntimeException("Matériel introuvable avec l'id: " + materielId));

        if (materiel.getStatus() != MaterielStatus.ACTIVE)
            throw new RuntimeException("Le matériel n'est pas disponible");

        int available = availabilityService.getAvailableQuantity(
                materielId, evenement.getDateDebut(), evenement.getDateFin());

        if (available < quantite)
            throw new RuntimeException("Stock insuffisant. Disponible: " + available);

        EvenementMateriel em = new EvenementMateriel();
        em.setEvenement(evenement);
        em.setMateriel(materiel);
        em.setQuantite(quantite);

        return evenementMaterielRepository.save(em);
    }

    @Override
    public EvenementMateriel updateQuantite(Long evenementMaterielId, int nouvelleQuantite) {
        EvenementMateriel em = evenementMaterielRepository.findById(evenementMaterielId)
                .orElseThrow(() -> new RuntimeException(
                        "Assignment introuvable avec l'id: " + evenementMaterielId));

        if (nouvelleQuantite <= 0)
            throw new RuntimeException("La quantité doit être supérieure à 0");

        Materiel materiel = em.getMateriel();
        Evenement evenement = em.getEvenement();

        // Use the centralized availability service
        int available = availabilityService.getAvailableQuantity(
                materiel.getId(),
                evenement.getDateDebut(),
                evenement.getDateFin());

        if (available < nouvelleQuantite)
            throw new RuntimeException(
                    "Stock insuffisant pour cette période. Disponible: " + available);

        em.setQuantite(nouvelleQuantite);
        return evenementMaterielRepository.save(em);
    }
    @Override
    public void retirerMateriel(Long evenementMaterielId) {
        if (!evenementMaterielRepository.existsById(evenementMaterielId))
            throw new RuntimeException("Assignment introuvable avec l'id: " + evenementMaterielId);
        evenementMaterielRepository.deleteById(evenementMaterielId);
    }

    @Override
    public List<EvenementMateriel> getByEvenement(Long evenementId) {
        return evenementMaterielRepository.findByEvenementId(evenementId);
    }

    @Override
    public List<EvenementMateriel> getByMateriel(Long materielId) {
        return evenementMaterielRepository.findByMaterielId(materielId);
    }
}