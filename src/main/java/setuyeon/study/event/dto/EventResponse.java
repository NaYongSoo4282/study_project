package setuyeon.study.event.dto;

import lombok.Getter;
import setuyeon.study.event.domain.Event;
import setuyeon.study.event.domain.EventStatus;

import java.time.LocalDateTime;

@Getter
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer capacity;
    private EventStatus status;

    public EventResponse(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.startAt = event.getStartAt();
        this.endAt = event.getEndAt();
        this.capacity = event.getCapacity();
        this.status = event.getStatus();
    }
}
