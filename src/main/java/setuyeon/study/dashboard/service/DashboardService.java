package setuyeon.study.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setuyeon.study.application.domain.ApplicationStatus;
import setuyeon.study.application.repository.ApplicationRepository;
import setuyeon.study.attendance.repository.AttendanceRepository;
import setuyeon.study.dashboard.dto.DashboardResponse;
import setuyeon.study.event.domain.EventStatus;
import setuyeon.study.event.repository.EventRepository;
import setuyeon.study.feedback.repository.FeedbackRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EventRepository eventRepository;
    private final ApplicationRepository applicationRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                eventRepository.count(),
                eventRepository.countByStatus(EventStatus.OPEN),
                applicationRepository.count(),
                applicationRepository.countByStatus(ApplicationStatus.APPROVED),
                attendanceRepository.count(),
                feedbackRepository.count()
        );
    }
}
