package org.example.rawabet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TimelineResponse {
    private Long userId;
    private SubscriptionDto currentSubscription;  // nullable
    private SubscriptionDto nextSubscription;     // nullable
    private List<SubscriptionDto> queuedSubscriptions;   // optional, sorted by dateDebut asc
    private List<SubscriptionDto> history;        // optional, sorted by dateDebut desc
}
