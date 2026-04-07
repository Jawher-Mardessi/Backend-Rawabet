package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import org.example.rawabet.dto.EvenementMaterielRequestDTO;
import org.example.rawabet.dto.EvenementRequestDTO;
import org.example.rawabet.dto.EvenementMaterielResponseDTO;
import org.example.rawabet.dto.EvenementResponseDTO;
import org.example.rawabet.entities.Evenement;
import org.example.rawabet.entities.EvenementMateriel;
import org.example.rawabet.entities.SalleEvenement;
import org.example.rawabet.enums.EvenementStatus;
import org.example.rawabet.services.IEvenementService;
import org.example.rawabet.services.ISalleEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    @Autowired
    private IEvenementService evenementService;

    @Autowired
    private ISalleEvenementService salleService;

    // ── Mapping helpers ──────────────────────────────────────────

    private Evenement toEntity(EvenementRequestDTO dto) {
        Evenement e = new Evenement();
        e.setTitre(dto.getTitre());
        e.setDescription(dto.getDescription());
        e.setDateDebut(dto.getDateDebut());
        e.setDateFin(dto.getDateFin());
        e.setNombreDePlaces(dto.getNombreDePlaces());
        if (dto.getSalleId() != null) {
            SalleEvenement salle = salleService.getSalleById(dto.getSalleId());
            e.setSalle(salle);
        }
        return e;
    }

    private EvenementResponseDTO toResponse(Evenement e) {
        EvenementResponseDTO dto = new EvenementResponseDTO();
        dto.setId(e.getId());
        dto.setTitre(e.getTitre());
        dto.setDescription(e.getDescription());
        dto.setDateDebut(e.getDateDebut());
        dto.setDateFin(e.getDateFin());
        dto.setNombreDePlaces(e.getNombreDePlaces());
        dto.setPlacesRestantes(evenementService.getRemainingPlaces(e.getId()));
        dto.setStatus(e.getStatus());
        if (e.getSalle() != null) {
            dto.setSalleId(e.getSalle().getId());
            dto.setSalleNom(e.getSalle().getNom());
            dto.setSalleType(e.getSalle().getType());
        }
        return dto;
    }

    private EvenementMaterielResponseDTO toEvenementMaterielResponse(EvenementMateriel em) {
        EvenementMaterielResponseDTO dto = new EvenementMaterielResponseDTO();
        dto.setId(em.getId());
        dto.setQuantite(em.getQuantite());
        dto.setEvenementId(em.getEvenement().getId());
        dto.setEvenementTitre(em.getEvenement().getTitre());
        dto.setMaterielId(em.getMateriel().getId());
        dto.setMaterielNom(em.getMateriel().getNom());
        dto.setMaterielReference(em.getMateriel().getReference());
        return dto;
    }

    // ── CRUD ─────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<EvenementResponseDTO> addEvenement(
            @Valid @RequestBody EvenementRequestDTO dto) {
        Evenement saved = evenementService.addEvenement(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvenementResponseDTO> updateEvenement(
            @PathVariable Long id,
            @Valid @RequestBody EvenementRequestDTO dto) {
        Evenement evenement = toEntity(dto);
        evenement.setId(id);
        Evenement updated = evenementService.updateEvenement(evenement);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvenementResponseDTO> getEvenementById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(evenementService.getEvenementById(id)));
    }

    @GetMapping
    public ResponseEntity<List<EvenementResponseDTO>> getAllEvenements() {
        List<EvenementResponseDTO> result = evenementService.getAllEvenements()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ── Salle assignment ─────────────────────────────────────────

    @PutMapping("/{id}/salle/{salleId}")
    public ResponseEntity<EvenementResponseDTO> assignSalle(
            @PathVariable Long id,
            @PathVariable Long salleId) {
        Evenement updated = evenementService.assignSalleToEvenement(id, salleId);
        return ResponseEntity.ok(toResponse(updated));
    }

    // ── Materiel assignment ──────────────────────────────────────

    @PostMapping("/materiels")
    public ResponseEntity<EvenementMaterielResponseDTO> assignMateriel(
            @Valid @RequestBody EvenementMaterielRequestDTO dto) {
        EvenementMateriel em = evenementService.assignMaterielToEvenement(
                dto.getEvenementId(), dto.getMaterielId(), dto.getQuantite());
        return ResponseEntity.status(HttpStatus.CREATED).body(toEvenementMaterielResponse(em));
    }

    @DeleteMapping("/materiels/{evenementMaterielId}")
    public ResponseEntity<Void> removeMateriel(@PathVariable Long evenementMaterielId) {
        evenementService.removeMaterielFromEvenement(evenementMaterielId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/materiels")
    public ResponseEntity<List<EvenementMaterielResponseDTO>> getMaterielsByEvenement(
            @PathVariable Long id) {
        List<EvenementMaterielResponseDTO> result = evenementService.getMaterielsByEvenement(id)
                .stream().map(this::toEvenementMaterielResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ── Availability ─────────────────────────────────────────────

    @GetMapping("/{id}/places-restantes")
    public ResponseEntity<Integer> getRemainingPlaces(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.getRemainingPlaces(id));
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> hasAvailablePlaces(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.hasAvailablePlaces(id));
    }

    // ── Filters ──────────────────────────────────────────────────

    @GetMapping("/upcoming")
    public ResponseEntity<List<EvenementResponseDTO>> getUpcomingEvenements() {
        List<EvenementResponseDTO> result = evenementService.getUpcomingEvenements()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/salle/{salleId}")
    public ResponseEntity<List<EvenementResponseDTO>> getEvenementsBySalle(
            @PathVariable Long salleId) {
        List<EvenementResponseDTO> result = evenementService.getEvenementsBySalle(salleId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<EvenementResponseDTO>> getEvenementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        List<EvenementResponseDTO> result = evenementService.getEvenementsByDateRange(dateDebut, dateFin)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<EvenementResponseDTO> updateStatus(
            @PathVariable Long id, @RequestParam EvenementStatus status) {
        return ResponseEntity.ok(toResponse(evenementService.updateStatus(id, status)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EvenementResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(evenementService.searchByKeyword(keyword)
                .stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EvenementResponseDTO>> getByStatus(@PathVariable EvenementStatus status) {
        return ResponseEntity.ok(evenementService.getEvenementsByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/waitlist-count")
    public ResponseEntity<Integer> getWaitlistCount(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.getWaitlistCount(id));
    }
}