package org.example.rawabet.dto;


/**
 * DTO combining Materiel with its ML prediction
 */
public class MaterielWithRiskDTO {

    private Long id;
    private String nom;
    private String type;
    private Integer quantiteTotale;
    private Double prixUnitaire;
    private String status;

    // Risk prediction fields
    private String riskLevel;
    private String riskBadge;
    private Double damageProbability;
    private String damageProbabilityPct;
    private String riskMessage;
    private Boolean needsMaintenance;
    private Boolean newMaterial;
    private Boolean mlAvailable;

    // Constructors
    public MaterielWithRiskDTO() {}

    public MaterielWithRiskDTO(Long id, String nom, String type, Integer qty,
                               Double prix, String status, PredictionResponse prediction) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.quantiteTotale = qty;
        this.prixUnitaire = prix;
        this.status = status;

        if (prediction != null && prediction.isAvailable()) {
            this.riskLevel = prediction.getPrediction();
            this.riskBadge = prediction.getRiskBadge();
            this.damageProbability = prediction.getDamageProbability();
            this.damageProbabilityPct = prediction.getDamageProbabilityPct();
            this.riskMessage = prediction.getMessage();
            this.needsMaintenance = prediction.isRiskyStatus();
            this.newMaterial = prediction.isNewMaterial();
            this.mlAvailable = true;
        } else {
            this.riskLevel = "UNKNOWN";
            this.riskBadge = "⚪ Unknown";
            this.needsMaintenance = false;
            this.newMaterial = false;
            this.mlAvailable = false;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getQuantiteTotale() { return quantiteTotale; }
    public void setQuantiteTotale(Integer quantiteTotale) { this.quantiteTotale = quantiteTotale; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRiskBadge() { return riskBadge; }
    public void setRiskBadge(String riskBadge) { this.riskBadge = riskBadge; }

    public Double getDamageProbability() { return damageProbability; }
    public void setDamageProbability(Double damageProbability) { this.damageProbability = damageProbability; }

    public String getDamageProbabilityPct() { return damageProbabilityPct; }
    public void setDamageProbabilityPct(String damageProbabilityPct) { this.damageProbabilityPct = damageProbabilityPct; }

    public String getRiskMessage() { return riskMessage; }
    public void setRiskMessage(String riskMessage) { this.riskMessage = riskMessage; }

    public Boolean getNeedsMaintenance() { return needsMaintenance; }
    public void setNeedsMaintenance(Boolean needsMaintenance) { this.needsMaintenance = needsMaintenance; }

    public Boolean getNewMaterial() { return newMaterial; }
    public void setNewMaterial(Boolean newMaterial) { this.newMaterial = newMaterial; }

    public Boolean getMlAvailable() { return mlAvailable; }
    public void setMlAvailable(Boolean mlAvailable) { this.mlAvailable = mlAvailable; }
}
