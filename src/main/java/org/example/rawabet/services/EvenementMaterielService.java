package org.example.rawabet.services;

import org.example.rawabet.entities.Evenement;
import org.example.rawabet.entities.EvenementMateriel;
import org.example.rawabet.entities.Materiel;
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

    @Override
    public EvenementMateriel assignerMateriel(Long evenementId, Long materielId, int quantite) {
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement introuvable avec l'id: " + evenementId));

        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new RuntimeException("Matériel introuvable avec l'id: " + materielId));

        if (!materiel.isDisponible())
            throw new RuntimeException("Le matériel n'est pas disponible");

        if (materiel.getQuantiteDisponible() < quantite)
            throw new RuntimeException("Stock insuffisant. Disponible: " + materiel.getQuantiteDisponible());

        EvenementMateriel em = new EvenementMateriel();
        em.setEvenement(evenement);
        em.setMateriel(materiel);
        em.setQuantite(quantite);

        return evenementMaterielRepository.save(em);
    }

    @Override
    public EvenementMateriel updateQuantite(Long evenementMaterielId, int nouvelleQuantite) {
        EvenementMateriel em = evenementMaterielRepository.findById(evenementMaterielId)
                .orElseThrow(() -> new RuntimeException("Assignment introuvable avec l'id: " + evenementMaterielId));

        if (em.getMateriel().getQuantiteDisponible() < nouvelleQuantite)
            throw new RuntimeException("Stock insuffisant. Disponible: " + em.getMateriel().getQuantiteDisponible());

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