package setuyeon.study.feedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setuyeon.study.attendance.repository.AttendanceRepository;
import setuyeon.study.event.domain.Event;
import setuyeon.study.event.domain.EventStatus;
import setuyeon.study.event.repository.EventRepository;
import setuyeon.study.feedback.domain.Feedback;
import setuyeon.study.feedback.dto.FeedbackCreateRequest;
import setuyeon.study.feedback.dto.FeedbackResponse;
import setuyeon.study.feedback.repository.FeedbackRepository;
import setuyeon.study.global.exception.BusinessException;
import setuyeon.study.global.exception.ErrorCode;
import setuyeon.study.member.domain.Member;
import setuyeon.study.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public FeedbackResponse createFeedback(Long eventId, FeedbackCreateRequest request, String email) {
        Event event = getEvent(eventId);
        Member member = getMember(email);

        if (event.getStatus() != EventStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.EVENT_NOT_COMPLETED);
        }

        if (!attendanceRepository.existsByEventIdAndMemberId(eventId, member.getId())) {
            throw new BusinessException(ErrorCode.FEEDBACK_ATTENDANCE_REQUIRED);
        }

        if (feedbackRepository.existsByEventIdAndMemberId(eventId, member.getId())) {
            throw new BusinessException(ErrorCode.ALREADY_FEEDBACK_SUBMITTED);
        }

        Feedback feedback = Feedback.createFeedback(event, member, request.getContent());
        return new FeedbackResponse(feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacks(Long eventId) {
        return feedbackRepository.findByEventId(eventId).stream()
                .map(FeedbackResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(FeedbackResponse::new)
                .toList();
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
