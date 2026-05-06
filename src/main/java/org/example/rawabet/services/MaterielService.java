package org.example.rawabet.services;

import org.example.rawabet.dto.MaterielOccupationDTO;
import org.example.rawabet.entities.EvenementMateriel;
import org.example.rawabet.entities.Materiel;
import org.example.rawabet.entities.ReservationMateriel;
import org.example.rawabet.enums.MaterielStatus;
import org.example.rawabet.repositories.CategorieMaterielRepository;
import org.example.rawabet.repositories.EvenementMaterielRepository;
import org.example.rawabet.repositories.MaterielRepository;
import org.example.rawabet.repositories.ReservationMaterielRepository;
import org.example.rawabet.services.IMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaterielService implements IMaterielService {
    @Autowired
    private ReservationMaterielRepository reservationMaterielRepository;

    @Autowired
    private EvenementMaterielRepository evenementMaterielRepository;
    @Autowired private MaterielRepository materielRepository;
    @Autowired private CategorieMaterielRepository categorieRepository;

    @Override
    public List<MaterielOccupationDTO> getFullOccupation(Long materielId) {
        List<MaterielOccupationDTO> result = new ArrayList<>();

        // ── 1. Standalone reservations ──────────────────────────────
        List<ReservationMateriel> reservations =
                reservationMaterielRepository.findByMaterielId(materielId);

        for (ReservationMateriel r : reservations) {
            MaterielOccupationDTO dto = new MaterielOccupationDTO();
            dto.setType("RESERVATION");
            dto.setQuantite(r.getQuantiteReservee());
            dto.setDateDebut(r.getDateDebut());
            dto.setDateFin(r.getDateFin());
            dto.setReservationId(r.getId());
            dto.setUserNom(r.getUser().getNom());
            dto.setStatut(r.getStatut().name());
            result.add(dto);
        }

        // ── 2. Event assignments ────────────────────────────────────
        List<EvenementMateriel> eventAssignments =
                evenementMaterielRepository.findByMaterielId(materielId);

        for (EvenementMateriel em : eventAssignments) {
            MaterielOccupationDTO dto = new MaterielOccupationDTO();
            dto.setType("EVENEMENT");
            dto.setQuantite(em.getQuantite());
            dto.setDateDebut(em.getEvenement().getDateDebut());
            dto.setDateFin(em.getEvenement().getDateFin());
            dto.setEvenementId(em.getEvenement().getId());
            dto.setEvenementTitre(em.getEvenement().getTitre());
            dto.setStatut(em.getEvenement().getStatus().name());
            result.add(dto);
        }

        // ── Sort by dateDebut ───────────────────────────────────────
        result.sort((a, b) -> a.getDateDebut().compareTo(b.getDateDebut()));

        return result;
    }
    @Override
    public Materiel addMateriel(Materiel materiel) {
        if (materiel.getStatus() == null) materiel.setStatus(MaterielStatus.ACTIVE);
        return materielRepository.save(materiel);
    }

    @Override
    public Materiel updateMateriel(Materiel materiel) {
        if (!materielRepository.existsById(materiel.getId()))
            throw new RuntimeException("Matériel introuvable avec l'id: " + materiel.getId());
        return materielRepository.save(materiel);
    }

    @Override
    public void deleteMateriel(Long id) {
        if (!materielRepository.existsById(id))
            throw new RuntimeException("Matériel introuvable avec l'id: " + id);
        materielRepository.deleteById(id);
    }

    @Override
    public Materiel getMaterielById(Long id) {
        return materielRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matériel introuvable avec l'id: " + id));
    }

    @Override
    public List<Materiel> getAllMateriels() {
        return materielRepository.findAll();
    }

    @Override
    public boolean isMaterielAvailable(Long materielId, int quantiteDemandee,
                                       LocalDateTime dateDebut, LocalDateTime dateFin) {
        return getAvailableQuantity(materielId, dateDebut, dateFin) >= quantiteDemandee;
    }

    @Override
    public int getAvailableQuantity(Long materielId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        Materiel materiel = getMaterielById(materielId);
        return materiel.getQuantiteTotale();
    }

    @Override
    public List<Materiel> getAvailableMateriels(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return materielRepository.findAll().stream()
                .filter(m -> m.getStatus() == MaterielStatus.ACTIVE)
                .toList();
    }

    @Override
    public List<Materiel> getMaterielsByCategorie(Long categorieId) {
        return materielRepository.findByCategorieId(categorieId);
    }

    @Override
    public Materiel updateStatus(Long id, MaterielStatus status) {
        Materiel materiel = getMaterielById(id);
        materiel.setStatus(status);
        return materielRepository.save(materiel);
    }

    @Override
    public List<Materiel> getMaterielsByStatus(MaterielStatus status) {
        return materielRepository.findByStatus(status);
    }
}