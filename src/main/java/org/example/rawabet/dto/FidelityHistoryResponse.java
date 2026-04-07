package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.ActionType;
import java.time.Instant;

@Data
@Builder
public class FidelityHistoryResponse {
    private ActionType action;
    private int points;
    private Instant createdAt;

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}