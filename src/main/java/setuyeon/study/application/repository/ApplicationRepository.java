package setuyeon.study.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setuyeon.study.application.domain.Application;
import setuyeon.study.application.domain.ApplicationStatus;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByEventIdAndMemberId(Long eventId, Long memberId);

    List<Application> findByEventId(Long eventId);

    long countByStatus(ApplicationStatus status);

    long countByEventIdAndStatus(Long eventId, ApplicationStatus status);
}
