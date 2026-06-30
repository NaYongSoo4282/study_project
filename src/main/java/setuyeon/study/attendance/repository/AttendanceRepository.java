package setuyeon.study.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setuyeon.study.attendance.domain.Attendance;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByEventIdAndMemberId(Long eventId, Long memberId);

    List<Attendance> findByEventId(Long eventId);
}
