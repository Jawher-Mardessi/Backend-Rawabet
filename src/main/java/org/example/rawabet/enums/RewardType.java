package org.example.rawabet.enums;

import lombok.Getter;

@Getter
public enum RewardType {

    CINEMA_FREE(200, "Séance cinéma gratuite"),
    EVENT_DISCOUNT(100, "Réduction 20% sur événement"),
    CLUB_DISCOUNT(150, "Réduction 15% sur événement club");

    private final int pointsCost;
    private final String description;

    RewardType(int pointsCost, String description) {
        this.pointsCost = pointsCost;
        this.description = description;
    }
}