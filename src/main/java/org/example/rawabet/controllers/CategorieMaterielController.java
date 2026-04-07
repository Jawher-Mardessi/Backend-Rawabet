package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import org.example.rawabet.dto.CategorieMaterielRequestDTO;
import org.example.rawabet.dto.CategorieMaterielResponseDTO;
import org.example.rawabet.entities.CategorieMateriel;
import org.example.rawabet.services.ICategorieMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories-materiel")
public class CategorieMaterielController {

    @Autowired
    private ICategorieMaterielService categorieService;

    // ── Mapping helpers ──────────────────────────────────────────

    private CategorieMateriel toEntity(CategorieMaterielRequestDTO dto) {
        CategorieMateriel c = new CategorieMateriel();
        c.setNom(dto.getNom());
        c.setDescription(dto.getDescription());
        return c;
    }

    private CategorieMaterielResponseDTO toResponse(CategorieMateriel c) {
        return new CategorieMaterielResponseDTO(c.getId(), c.getNom(), c.getDescription());
    }

    // ── CRUD ─────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<CategorieMaterielResponseDTO> addCategorie(
            @Valid @RequestBody CategorieMaterielRequestDTO dto) {
        CategorieMateriel saved = categorieService.addCategorie(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieMaterielResponseDTO> updateCategorie(
            @PathVariable Long id,
            @Valid @RequestBody CategorieMaterielRequestDTO dto) {
        CategorieMateriel categorie = toEntity(dto);
        categorie.setId(id);
        CategorieMateriel updated = categorieService.updateCategorie(categorie);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieMaterielResponseDTO> getCategorieById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(categorieService.getCategorieById(id)));
    }

    @GetMapping
    public ResponseEntity<List<CategorieMaterielResponseDTO>> getAllCategories() {
        List<CategorieMaterielResponseDTO> result = categorieService.getAllCategories()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}