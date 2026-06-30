package setuyeon.study.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setuyeon.study.feedback.domain.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventId(Long eventId);

    boolean existsByEventIdAndMemberId(Long eventId, Long memberId);
}
