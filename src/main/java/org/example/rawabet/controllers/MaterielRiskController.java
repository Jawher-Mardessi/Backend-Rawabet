package org.example.rawabet.controllers;

import org.example.rawabet.dto.MaterielWithRiskDTO;
import org.example.rawabet.services.MaterielRiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/materiel")
@CrossOrigin(origins = "*")
public class MaterielRiskController {

    private final MaterielRiskService riskService;

    public MaterielRiskController(MaterielRiskService riskService) {
        this.riskService = riskService;
    }

    /**
     * Get all materials with risk assessment
     */
    @GetMapping("/with-risk")
    public ResponseEntity<List<MaterielWithRiskDTO>> getAllWithRisk() {
        return ResponseEntity.ok(riskService.getAllWithRisk());
    }

    /**
     * Get materials needing maintenance (HIGH or MODERATE risk)
     */
    @GetMapping("/maintenance-needed")
    public ResponseEntity<List<MaterielWithRiskDTO>> getMaterialsNeedingMaintenance() {
        List<MaterielWithRiskDTO> materials = riskService.getMaterialsNeedingMaintenance();
        return ResponseEntity.ok(materials);
    }

    /**
     * Get new materials (insufficient data)
     */
    @GetMapping("/new-materials")
    public ResponseEntity<List<MaterielWithRiskDTO>> getNewMaterials() {
        List<MaterielWithRiskDTO> materials = riskService.getNewMaterials();
        return ResponseEntity.ok(materials);
    }

    /**
     * Get safe materials
     */
    @GetMapping("/safe")
    public ResponseEntity<List<MaterielWithRiskDTO>> getSafeMaterials() {
        List<MaterielWithRiskDTO> materials = riskService.getSafeMaterials();
        return ResponseEntity.ok(materials);
    }

    /**
     * Get single material with risk
     */
    @GetMapping("/{id}/with-risk")
    public ResponseEntity<MaterielWithRiskDTO> getMaterielWithRisk(@PathVariable Long id) {
        MaterielWithRiskDTO materiel = riskService.getMaterielWithRisk(id);
        return ResponseEntity.ok(materiel);
    }

    /**
     * Get risk prediction summary
     */
    @GetMapping("/risk-summary")
    public ResponseEntity<?> getRiskSummary() {
        List<MaterielWithRiskDTO> all = riskService.getAllWithRisk();
        List<MaterielWithRiskDTO> risky = riskService.getMaterialsNeedingMaintenance();
        List<MaterielWithRiskDTO> safe = riskService.getSafeMaterials();
        List<MaterielWithRiskDTO> newMat = riskService.getNewMaterials();

        return ResponseEntity.ok(Map.of(
                "total_materials", all.size(),
                "materials_at_risk", risky.size(),
                "materials_safe", safe.size(),
                "new_materials", newMat.size(),
                "at_risk_percentage", all.isEmpty() ? 0 : Math.round((risky.size() * 100.0) / all.size())
        ));
    }

    /**
     * Get high-risk materials requiring immediate attention
     */
    @GetMapping("/high-risk")
    public ResponseEntity<List<MaterielWithRiskDTO>> getHighRiskMaterials() {
        return ResponseEntity.ok(
                riskService.getAllWithRisk().stream()
                        .filter(m -> "AT_RISK".equals(m.getRiskLevel()))
                        .toList()
        );
    }
}
