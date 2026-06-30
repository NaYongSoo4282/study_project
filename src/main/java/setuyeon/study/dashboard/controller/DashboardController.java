package setuyeon.study.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import setuyeon.study.dashboard.dto.DashboardResponse;
import setuyeon.study.dashboard.service.DashboardService;
import setuyeon.study.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/v1/admin/dashboard")
    public ApiResponse<DashboardResponse> getDashboard() {
        return ApiResponse.success("대시보드 조회 완료", dashboardService.getDashboard());
    }
}
