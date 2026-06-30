package setuyeon.study.event.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    protected Event() {
    }

    public static Event createEvent(String title, String description, String location,
                                    LocalDateTime startAt, LocalDateTime endAt, Integer capacity) {
        Event event = new Event();
        event.title = title;
        event.description = description;
        event.location = location;
        event.startAt = startAt;
        event.endAt = endAt;
        event.capacity = capacity;
        event.status = EventStatus.DRAFT;
        return event;
    }

    public void update(String title, String description, String location,
                       LocalDateTime startAt, LocalDateTime endAt, Integer capacity) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startAt = startAt;
        this.endAt = endAt;
        this.capacity = capacity;
    }

    public void updateStatus(EventStatus status) {
        this.status = status;
    }
}
