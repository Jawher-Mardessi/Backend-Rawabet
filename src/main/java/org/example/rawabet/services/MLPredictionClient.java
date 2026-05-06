package org.example.rawabet.services;

import org.example.rawabet.dto.PredictionResponse;
import org.example.rawabet.enums.RiskLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

@Service
public class MLPredictionClient {

    private static final Logger logger = Logger.getLogger(MLPredictionClient.class.getName());

    @Value("${ml.api.url:http://localhost:5000}")
    private String mlApiUrl;

    private final RestTemplate restTemplate;

    public MLPredictionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get damage risk prediction for a material
     * @param materielName Material name (e.g., "Projecteur_HD" or "Projecteur_HD_50")
     * @return PredictionResponse with risk assessment
     */
    public PredictionResponse predictMaterielRisk(String materielName) {
        try {
            String url = mlApiUrl + "/predict/materiel/" + materielName;

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);

            return mapToPredictionResponse(response);
        } catch (RestClientException e) {
            logger.log(Level.WARNING, "ML API call failed for material: " + materielName, e);
            return PredictionResponse.unavailable("ML API unavailable");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error predicting risk for material: " + materielName, e);
            return PredictionResponse.error("Prediction failed: " + e.getMessage());
        }
    }

    /**
     * Check if material needs maintenance
     * @param materielName Material name
     * @return true if material is AT_RISK or needs attention
     */
    public boolean needsMaintenance(String materielName) {
        PredictionResponse prediction = predictMaterielRisk(materielName);
        return prediction != null && prediction.isRiskyStatus();
    }

    /**
     * Get risk level badge
     * @param materielName Material name
     * @return Risk level enum (AT_RISK, SAFE, INSUFFICIENT_DATA, etc.)
     */
    public RiskLevel getRiskLevel(String materielName) {
        PredictionResponse prediction = predictMaterielRisk(materielName);
        if (prediction == null) {
            return RiskLevel.UNKNOWN;
        }
        return RiskLevel.fromString(prediction.getPrediction());
    }

    /**
     * Map API response to PredictionResponse object
     */
    private PredictionResponse mapToPredictionResponse(Map<String, Object> response) {
        if (response == null) {
            return null;
        }

        PredictionResponse pred = new PredictionResponse();
        pred.setMaterielName((String) response.get("materiel_name"));
        pred.setMaterielType((String) response.get("materiel_type"));
        pred.setPrediction((String) response.get("prediction"));
        pred.setMessage((String) response.get("message"));
        pred.setColdStart((Boolean) response.getOrDefault("cold_start", false));

        Object damageProbObj = response.get("damage_probability_30d");
        if (damageProbObj != null && damageProbObj instanceof Number) {
            pred.setDamageProbability(((Number) damageProbObj).doubleValue());
        }

        String probStr = (String) response.get("damage_probability_pct");
        if (probStr != null) {
            pred.setDamageProbabilityPct(probStr);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> features = (Map<String, Object>) response.get("features_used");
        pred.setFeaturesUsed(features);

        Object note = response.get("note");
        if (note != null) {
            pred.setNote((String) note);
        }

        return pred;
    }
}
