package org.example.rawabet.services;

import java.time.LocalDateTime;

public interface IAvailabilityService {
    int getAvailableQuantity(Long materielId, LocalDateTime startDate, LocalDateTime endDate);
}
