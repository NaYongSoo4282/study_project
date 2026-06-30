package setuyeon.study.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import setuyeon.study.application.dto.ApplicationResponse;
import setuyeon.study.application.dto.ApplicationStatusUpdateRequest;
import setuyeon.study.application.service.ApplicationService;
import setuyeon.study.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/{eventId}/applications")
    public ApiResponse<ApplicationResponse> apply(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success("행사 신청 완료",
                applicationService.apply(eventId, userDetails.getUsername()));
    }

    @GetMapping("/{eventId}/applications/me")
    public ApiResponse<ApplicationResponse> getMyApplication(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success("내 신청 상태 조회 완료",
                applicationService.getMyApplication(eventId, userDetails.getUsername()));
    }

    @DeleteMapping("/{eventId}/applications/me")
    public ApiResponse<Void> cancelMyApplication(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        applicationService.cancelMyApplication(eventId, userDetails.getUsername());
        return ApiResponse.success("내 신청 취소 완료", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{eventId}/applications")
    public ApiResponse<List<ApplicationResponse>> getApplications(@PathVariable Long eventId) {
        return ApiResponse.success("신청자 목록 조회 완료",
                applicationService.getApplications(eventId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{eventId}/applications/{applicationId}/status")
    public ApiResponse<ApplicationResponse> updateStatus(
            @PathVariable Long eventId,
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusUpdateRequest request
    ) {
        return ApiResponse.success("신청 상태 변경 완료",
                applicationService.updateStatus(eventId, applicationId, request));
    }
}
