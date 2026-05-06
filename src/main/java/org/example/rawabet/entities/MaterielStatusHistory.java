package org.example.rawabet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.example.rawabet.enums.MaterielStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "materiel_status_history")
public class MaterielStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "materiel_id", nullable = false)
    private Long materielId;

    @Column(name = "materiel_name", nullable = false)
    private String materielName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterielStatus status;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public MaterielStatusHistory() {
    }

    public MaterielStatusHistory(Long id, Long materielId, String materielName, MaterielStatus status, LocalDateTime changedAt) {
        this.id = id;
        this.materielId = materielId;
        this.materielName = materielName;
        this.status = status;
        this.changedAt = changedAt;
    }

    @PrePersist
    public void onCreate() {
        if (this.changedAt == null) {
            this.changedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMaterielId() {
        return materielId;
    }

    public void setMaterielId(Long materielId) {
        this.materielId = materielId;
    }

    public String getMaterielName() {
        return materielName;
    }

    public void setMaterielName(String materielName) {
        this.materielName = materielName;
    }

    public MaterielStatus getStatus() {
        return status;
    }

    public void setStatus(MaterielStatus status) {
        this.status = status;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}

