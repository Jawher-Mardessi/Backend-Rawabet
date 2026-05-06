package org.example.rawabet.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class PredictionResponse {

    @JsonProperty("materiel_name")
    private String materielName;

    @JsonProperty("materiel_type")
    private String materielType;

    @JsonProperty("prediction")
    private String prediction;

    @JsonProperty("damage_probability_30d")
    private Double damageProbability;

    @JsonProperty("damage_probability_pct")
    private String damageProbabilityPct;

    @JsonProperty("message")
    private String message;

    @JsonProperty("cold_start")
    private Boolean coldStart;

    @JsonProperty("features_used")
    private Map<String, Object> featuresUsed;

    @JsonProperty("note")
    private String note;

    private String error;

    // Constructors
    public PredictionResponse() {}

    public static PredictionResponse unavailable(String message) {
        PredictionResponse p = new PredictionResponse();
        p.error = message;
        p.prediction = "UNAVAILABLE";
        return p;
    }

    public static PredictionResponse error(String message) {
        PredictionResponse p = new PredictionResponse();
        p.error = message;
        p.prediction = "ERROR";
        return p;
    }

    // Utility methods
    public boolean isRiskyStatus() {
        return prediction != null && (
                prediction.equals("AT_RISK") ||
                        prediction.equals("MODERATE_RISK") ||
                        (damageProbability != null && damageProbability > 0.5)
        );
    }

    public boolean isSafe() {
        return prediction != null && prediction.equals("SAFE");
    }

    public boolean isNewMaterial() {
        return prediction != null && prediction.equals("INSUFFICIENT_DATA");
    }

    public boolean isAvailable() {
        return error == null && prediction != null && !prediction.equals("UNAVAILABLE");
    }

    public String getRiskBadge() {
        if (!isAvailable()) {
            return "⚠️ UNKNOWN";
        }

        switch (prediction) {
            case "AT_RISK":
                return "🔴 AT RISK (" + damageProbabilityPct + ")";
            case "MODERATE_RISK":
                return "🟡 MODERATE (" + damageProbabilityPct + ")";
            case "SAFE":
                return "🟢 SAFE (" + damageProbabilityPct + ")";
            case "INSUFFICIENT_DATA":
                return "🔵 NEW MATERIAL";
            default:
                return "⚪ " + prediction;
        }
    }

    // Getters and Setters
    public String getMaterielName() { return materielName; }
    public void setMaterielName(String materielName) { this.materielName = materielName; }

    public String getMaterielType() { return materielType; }
    public void setMaterielType(String materielType) { this.materielType = materielType; }

    public String getPrediction() { return prediction; }
    public void setPrediction(String prediction) { this.prediction = prediction; }

    public Double getDamageProbability() { return damageProbability; }
    public void setDamageProbability(Double damageProbability) { this.damageProbability = damageProbability; }

    public String getDamageProbabilityPct() { return damageProbabilityPct; }
    public void setDamageProbabilityPct(String damageProbabilityPct) { this.damageProbabilityPct = damageProbabilityPct; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getColdStart() { return coldStart; }
    public void setColdStart(Boolean coldStart) { this.coldStart = coldStart; }

    public Map<String, Object> getFeaturesUsed() { return featuresUsed; }
    public void setFeaturesUsed(Map<String, Object> featuresUsed) { this.featuresUsed = featuresUsed; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    @Override
    public String toString() {
        return "PredictionResponse{" +
                "materielName='" + materielName + '\'' +
                ", prediction='" + prediction + '\'' +
                ", damageProbabilityPct='" + damageProbabilityPct + '\'' +
                '}';
    }
}
