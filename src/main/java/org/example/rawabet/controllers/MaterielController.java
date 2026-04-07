package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import org.example.rawabet.dto.MaterielRequestDTO;
import org.example.rawabet.dto.MaterielResponseDTO;
import org.example.rawabet.entities.CategorieMateriel;
import org.example.rawabet.entities.Materiel;
import org.example.rawabet.enums.MaterielStatus;
import org.example.rawabet.services.ICategorieMaterielService;
import org.example.rawabet.services.IMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/materiels")
public class MaterielController {

    @Autowired
    private IMaterielService materielService;

    @Autowired
    private ICategorieMaterielService categorieService;

    // ── Mapping helpers ──────────────────────────────────────────

    private Materiel toEntity(MaterielRequestDTO dto) {
        Materiel m = new Materiel();
        m.setNom(dto.getNom());
        m.setDescription(dto.getDescription());
        m.setReference(dto.getReference());
        m.setQuantiteDisponible(dto.getQuantiteDisponible());
        m.setPrixUnitaire(dto.getPrixUnitaire());
        m.setDisponible(dto.isDisponible());
        if (dto.getCategorieId() != null) {
            CategorieMateriel categorie = categorieService.getCategorieById(dto.getCategorieId());
            m.setCategorie(categorie);
        }
        return m;
    }

    private MaterielResponseDTO toResponse(Materiel m) {
        MaterielResponseDTO dto = new MaterielResponseDTO();
        dto.setId(m.getId());
        dto.setNom(m.getNom());
        dto.setDescription(m.getDescription());
        dto.setReference(m.getReference());
        dto.setQuantiteDisponible(m.getQuantiteDisponible());
        dto.setPrixUnitaire(m.getPrixUnitaire());
        dto.setDisponible(m.isDisponible());
        dto.setStatus(m.getStatus());
        if (m.getCategorie() != null) {
            dto.setCategorieId(m.getCategorie().getId());
            dto.setCategorieNom(m.getCategorie().getNom());
        }
        return dto;
    }
    // ── CRUD ─────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<MaterielResponseDTO> addMateriel(
            @Valid @RequestBody MaterielRequestDTO dto) {
        Materiel saved = materielService.addMateriel(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterielResponseDTO> updateMateriel(
            @PathVariable Long id,
            @Valid @RequestBody MaterielRequestDTO dto) {
        Materiel materiel = toEntity(dto);
        materiel.setId(id);
        Materiel updated = materielService.updateMateriel(materiel);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMateriel(@PathVariable Long id) {
        materielService.deleteMateriel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterielResponseDTO> getMaterielById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(materielService.getMaterielById(id)));
    }

    @GetMapping
    public ResponseEntity<List<MaterielResponseDTO>> getAllMateriels() {
        List<MaterielResponseDTO> result = materielService.getAllMateriels()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ── Availability ─────────────────────────────────────────────

    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> isMaterielAvailable(
            @PathVariable Long id,
            @RequestParam int quantite,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        return ResponseEntity.ok(materielService.isMaterielAvailable(id, quantite, dateDebut, dateFin));
    }

    @GetMapping("/{id}/available-quantity")
    public ResponseEntity<Integer> getAvailableQuantity(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        return ResponseEntity.ok(materielService.getAvailableQuantity(id, dateDebut, dateFin));
    }

    @GetMapping("/available")
    public ResponseEntity<List<MaterielResponseDTO>> getAvailableMateriels(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        List<MaterielResponseDTO> result = materielService.getAvailableMateriels(dateDebut, dateFin)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ── Filters ──────────────────────────────────────────────────

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<MaterielResponseDTO>> getMaterielsByCategorie(
            @PathVariable Long categorieId) {
        List<MaterielResponseDTO> result = materielService.getMaterielsByCategorie(categorieId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @PatchMapping("/{id}/toggle-disponible")
    public ResponseEntity<MaterielResponseDTO> toggleDisponible(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(materielService.toggleDisponible(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MaterielResponseDTO> updateStatus(
            @PathVariable Long id, @RequestParam MaterielStatus status) {
        return ResponseEntity.ok(toResponse(materielService.updateStatus(id, status)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MaterielResponseDTO>> getByStatus(@PathVariable MaterielStatus status) {
        return ResponseEntity.ok(materielService.getMaterielsByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList()));
    }
}