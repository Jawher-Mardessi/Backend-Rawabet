package org.example.rawabet.enums;


public enum RiskLevel {
    AT_RISK("AT_RISK", "🔴", "High Risk"),
    MODERATE_RISK("MODERATE_RISK", "🟡", "Moderate Risk"),
    SAFE("SAFE", "🟢", "Safe"),
    INSUFFICIENT_DATA("INSUFFICIENT_DATA", "🔵", "New Material"),
    UNKNOWN("UNKNOWN", "⚪", "Unknown"),
    UNAVAILABLE("UNAVAILABLE", "⚠️", "Unavailable");

    private final String code;
    private final String emoji;
    private final String label;

    RiskLevel(String code, String emoji, String label) {
        this.code = code;
        this.emoji = emoji;
        this.label = label;
    }

    public static RiskLevel fromString(String value) {
        if (value == null) {
            return UNKNOWN;
        }

        for (RiskLevel level : RiskLevel.values()) {
            if (level.code.equalsIgnoreCase(value)) {
                return level;
            }
        }
        return UNKNOWN;
    }

    public String getCode() { return code; }
    public String getEmoji() { return emoji; }
    public String getLabel() { return label; }

    public String getBadge() {
        return emoji + " " + label;
    }

    public boolean isRisky() {
        return this == AT_RISK || this == MODERATE_RISK;
    }
}
