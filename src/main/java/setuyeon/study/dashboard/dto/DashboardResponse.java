package setuyeon.study.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponse {
    private long totalEventCount;
    private long openEventCount;
    private long totalApplicationCount;
    private long approvedApplicationCount;
    private long attendanceCount;
    private long feedbackCount;
}
