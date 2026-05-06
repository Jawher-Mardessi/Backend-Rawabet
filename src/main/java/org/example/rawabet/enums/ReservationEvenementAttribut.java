package org.example.rawabet.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReservationEvenementAttribut {
    CONFIRMED("Confirmed"),
    ALREADY_USED("Already Used");

    private final String value;

    ReservationEvenementAttribut(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ReservationEvenementAttribut fromValue(String value) {
        for (ReservationEvenementAttribut attribut : values()) {
            if (attribut.value.equalsIgnoreCase(value) || attribut.name().equalsIgnoreCase(value)) {
                return attribut;
            }
        }
        throw new IllegalArgumentException("Unknown ReservationEvenementAttribut: " + value);
    }
}
