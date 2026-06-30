package setuyeon.study.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import setuyeon.study.attendance.dto.AttendanceCheckRequest;
import setuyeon.study.attendance.dto.AttendanceCodeResponse;
import setuyeon.study.attendance.dto.AttendanceResponse;
import setuyeon.study.attendance.service.AttendanceCodeService;
import setuyeon.study.attendance.service.AttendanceService;
import setuyeon.study.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceCodeService attendanceCodeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/events/{eventId}/attendance-codes")
    public ApiResponse<AttendanceCodeResponse> createAttendanceCode(
            @PathVariable Long eventId
    ) {
        return ApiResponse.success("출석 코드 생성 완료",
                attendanceCodeService.createCode(eventId));
    }

    @PostMapping("/events/{eventId}/attendances")
    public ApiResponse<AttendanceResponse> checkAttendance(
            @PathVariable Long eventId,
            @RequestBody AttendanceCheckRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success("출석 체크 완료",
                attendanceService.checkAttendance(eventId, request, userDetails.getUsername()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/events/{eventId}/attendances")
    public ApiResponse<List<AttendanceResponse>> getAttendances(@PathVariable Long eventId) {
        return ApiResponse.success("출석 현황 조회 완료",
                attendanceService.getAttendances(eventId));
    }
}
