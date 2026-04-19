package org.example.rawabet.dto.abonnement;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubscriptionTimelineDTO {

    /** The subscription that is currently active (null if none). */
    private UserAbonnementDTO currentSubscription;

    /** The first upcoming queued subscription (null if none). */
    private UserAbonnementDTO nextSubscription;

    /** All queued subscriptions sorted by dateDebut ascending (may be empty). */
    private List<UserAbonnementDTO> queuedSubscriptions;

    /** History of expired / exhausted subscriptions sorted by dateDebut descending. */
    private List<UserAbonnementDTO> history;
}
