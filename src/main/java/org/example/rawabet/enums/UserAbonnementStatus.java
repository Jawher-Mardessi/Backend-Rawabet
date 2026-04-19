package org.example.rawabet.enums;

public enum UserAbonnementStatus {
    /** Subscription is currently active (within start/end dates and tickets remain). */
    ACTIVE,
    /** Subscription is purchased but its start date is in the future. */
    QUEUED,
    /** Subscription has passed its end date. */
    EXPIRED,
    /** Subscription is within its date range but all tickets have been consumed. */
    EXHAUSTED
}
