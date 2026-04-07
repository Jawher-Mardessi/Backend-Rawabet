package org.example.rawabet.dto;

public class EvenementMaterielResponseDTO {

    private Long id;
    private int quantite;
    private Long evenementId;
    private String evenementTitre;
    private Long materielId;
    private String materielNom;
    private String materielReference;

    public EvenementMaterielResponseDTO() {}

    public EvenementMaterielResponseDTO(Long id, int quantite, Long evenementId,
                                        String evenementTitre, Long materielId,
                                        String materielNom, String materielReference) {
        this.id = id;
        this.quantite = quantite;
        this.evenementId = evenementId;
        this.evenementTitre = evenementTitre;
        this.materielId = materielId;
        this.materielNom = materielNom;
        this.materielReference = materielReference;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public Long getEvenementId() { return evenementId; }
    public void setEvenementId(Long evenementId) { this.evenementId = evenementId; }

    public String getEvenementTitre() { return evenementTitre; }
    public void setEvenementTitre(String evenementTitre) { this.evenementTitre = evenementTitre; }

    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public String getMaterielNom() { return materielNom; }
    public void setMaterielNom(String materielNom) { this.materielNom = materielNom; }

    public String getMaterielReference() { return materielReference; }
    public void setMaterielReference(String materielReference) { this.materielReference = materielReference; }
}
