package org.example.rawabet.enums;

public enum SubscriptionStatus {
    ACTIVE,      // Currently active subscription
    QUEUED,      // Future subscription waiting to start
    EXPIRED,     // Past subscription (dateDebut < today and dateFin < today)
    EXHAUSTED    // Non-unlimited plan with no tickets remaining but still valid date
}
