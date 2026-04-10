package org.example.rawabet.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SeanceDeletedEvent extends ApplicationEvent {

    private final Long seanceId;

    public SeanceDeletedEvent(Object source, Long seanceId) {
        super(source);
        this.seanceId = seanceId;
    }
}