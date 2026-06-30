package setuyeon.study.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setuyeon.study.event.domain.Event;
import setuyeon.study.event.domain.EventStatus;
import setuyeon.study.event.dto.EventCreateRequest;
import setuyeon.study.event.dto.EventResponse;
import setuyeon.study.event.dto.EventStatusUpdateRequest;
import setuyeon.study.event.dto.EventUpdateRequest;
import setuyeon.study.event.repository.EventRepository;
import setuyeon.study.global.exception.BusinessException;
import setuyeon.study.global.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<EventResponse> getEvents(EventStatus status) {
        List<Event> events = status == null ? eventRepository.findAll() : eventRepository.findByStatus(status);
        return events.stream()
                .map(EventResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(Long eventId) {
        Event event = getEventEntity(eventId);
        return new EventResponse(event);
    }

    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        Event event = Event.createEvent(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getStartAt(),
                request.getEndAt(),
                request.getCapacity()
        );

        return new EventResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventUpdateRequest request) {
        Event event = getEventEntity(eventId);
        event.update(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getStartAt(),
                request.getEndAt(),
                request.getCapacity()
        );

        return new EventResponse(event);
    }

    @Transactional
    public EventResponse updateStatus(Long eventId, EventStatusUpdateRequest request) {
        Event event = getEventEntity(eventId);
        event.updateStatus(request.getStatus());
        return new EventResponse(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = getEventEntity(eventId);
        eventRepository.delete(event);
    }

    public Event getEventEntity(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
    }
}
