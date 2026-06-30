package setuyeon.study.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import setuyeon.study.event.domain.EventStatus;
import setuyeon.study.event.dto.EventCreateRequest;
import setuyeon.study.event.dto.EventResponse;
import setuyeon.study.event.dto.EventStatusUpdateRequest;
import setuyeon.study.event.dto.EventUpdateRequest;
import setuyeon.study.event.service.EventService;
import setuyeon.study.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;

    @GetMapping("/events")
    public ApiResponse<List<EventResponse>> getEvents(@RequestParam(required = false) EventStatus status) {
        return ApiResponse.success("행사 목록 조회 완료", eventService.getEvents(status));
    }

    @GetMapping("/events/{eventId}")
    public ApiResponse<EventResponse> getEvent(@PathVariable Long eventId) {
        return ApiResponse.success("행사 상세 조회 완료", eventService.getEvent(eventId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/events")
    public ApiResponse<EventResponse> createEvent(@RequestBody EventCreateRequest request) {
        return ApiResponse.success("행사 생성 완료", eventService.createEvent(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/events/{eventId}")
    public ApiResponse<EventResponse> updateEvent(@PathVariable Long eventId, @RequestBody EventUpdateRequest request) {
        return ApiResponse.success("행사 수정 완료", eventService.updateEvent(eventId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/events/{eventId}/status")
    public ApiResponse<EventResponse> updateStatus(@PathVariable Long eventId, @RequestBody EventStatusUpdateRequest request) {
        return ApiResponse.success("행사 상태 변경 완료", eventService.updateStatus(eventId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/events/{eventId}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ApiResponse.success("행사 삭제 완료", null);
    }
}
