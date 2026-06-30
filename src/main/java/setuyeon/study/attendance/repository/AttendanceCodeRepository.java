package setuyeon.study.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setuyeon.study.attendance.domain.AttendanceCode;

import java.util.Optional;

public interface AttendanceCodeRepository extends JpaRepository<AttendanceCode, Long> {
    Optional<AttendanceCode> findTopByEventIdOrderByIdDesc(Long eventId);
}
