package org.example.rawabet.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SeanceCreatedEvent extends ApplicationEvent {

    private final Long seanceId;
    private final String filmTitle;
    private final String seanceTime;   // format "HH:mm"
    private final int durationMinutes; // 0 si non renseigné

    public SeanceCreatedEvent(Object source, Long seanceId,
                              String filmTitle, String seanceTime,
                              int durationMinutes) {
        super(source);
        this.seanceId = seanceId;
        this.filmTitle = filmTitle;
        this.seanceTime = seanceTime;
        this.durationMinutes = durationMinutes;
    }
}