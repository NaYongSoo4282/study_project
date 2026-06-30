package setuyeon.study.event.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventCreateRequest {
    private String title;
    private String description;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer capacity;
}
