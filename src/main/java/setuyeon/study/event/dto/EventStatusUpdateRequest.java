package setuyeon.study.event.dto;

import lombok.Getter;
import setuyeon.study.event.domain.EventStatus;

@Getter
public class EventStatusUpdateRequest {
    private EventStatus status;
}
