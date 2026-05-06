package org.example.rawabet.services;

import org.example.rawabet.dto.MaterielWithRiskDTO;
import org.example.rawabet.dto.PredictionResponse;
import org.example.rawabet.entities.Materiel;
import org.example.rawabet.repositories.MaterielRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterielRiskService {

    private final MaterielRepository materielRepository;
    private final MLPredictionClient mlClient;

    public MaterielRiskService(MaterielRepository materielRepository, MLPredictionClient mlClient) {
        this.materielRepository = materielRepository;
        this.mlClient = mlClient;
    }

    /**
     * Get all materials with risk predictions
     */
    public List<MaterielWithRiskDTO> getAllWithRisk() {
        return materielRepository.findAll().stream()
                .map(this::toMaterielWithRisk)
                .collect(Collectors.toList());
    }

    /**
     * Get all materials that need maintenance (AT_RISK or MODERATE_RISK)
     */
    public List<MaterielWithRiskDTO> getMaterialsNeedingMaintenance() {
        return getAllWithRisk().stream()
                .filter(m -> Boolean.TRUE.equals(m.getNeedsMaintenance()))
                .collect(Collectors.toList());
    }

    /**
     * Get all new materials (INSUFFICIENT_DATA)
     */
    public List<MaterielWithRiskDTO> getNewMaterials() {
        return getAllWithRisk().stream()
                .filter(m -> Boolean.TRUE.equals(m.getNewMaterial()))
                .collect(Collectors.toList());
    }

    /**
     * Get all safe materials
     */
    public List<MaterielWithRiskDTO> getSafeMaterials() {
        return getAllWithRisk().stream()
                .filter(m -> "SAFE".equals(m.getRiskLevel()))
                .collect(Collectors.toList());
    }

    /**
     * Get single material with risk prediction
     */
    public MaterielWithRiskDTO getMaterielWithRisk(Long id) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        return toMaterielWithRisk(materiel);
    }

    /**
     * Get risk prediction for a material name
     */
    public PredictionResponse getPredictionForMaterial(String materielName) {
        return mlClient.predictMaterielRisk(materielName);
    }

    /**
     * Create material and get prediction
     */
    public MaterielWithRiskDTO createMaterielWithRisk(Materiel materiel) {
        Materiel saved = materielRepository.save(materiel);
        return toMaterielWithRisk(saved);
    }

    /**
     * Check if material needs maintenance
     */
    public boolean materielNeedsMaintenance(Long id) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        return mlClient.needsMaintenance(materiel.getNom());
    }

    /**
     * Convert Materiel to MaterielWithRiskDTO
     */
    private MaterielWithRiskDTO toMaterielWithRisk(Materiel materiel) {
        PredictionResponse prediction = mlClient.predictMaterielRisk(materiel.getNom());

        return new MaterielWithRiskDTO(
                materiel.getId(),
                materiel.getNom(),
                materiel.getNom(),  // Use material name as type for ML predictions
                materiel.getQuantiteTotale(),
                materiel.getPrixUnitaire(),
                materiel.getStatus() != null ? materiel.getStatus().toString() : "UNKNOWN",
                prediction
        );
    }
}
