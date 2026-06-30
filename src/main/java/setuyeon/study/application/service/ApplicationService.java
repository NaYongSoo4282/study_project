package setuyeon.study.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setuyeon.study.application.domain.Application;
import setuyeon.study.application.domain.ApplicationStatus;
import setuyeon.study.application.dto.ApplicationResponse;
import setuyeon.study.application.dto.ApplicationStatusUpdateRequest;
import setuyeon.study.application.repository.ApplicationRepository;
import setuyeon.study.event.domain.Event;
import setuyeon.study.event.domain.EventStatus;
import setuyeon.study.event.repository.EventRepository;
import setuyeon.study.global.exception.BusinessException;
import setuyeon.study.global.exception.ErrorCode;
import setuyeon.study.member.domain.Member;
import setuyeon.study.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ApplicationResponse apply(Long eventId, String email) {
        Event event = getEvent(eventId);
        Member member = getMember(email);

        if (event.getStatus() != EventStatus.OPEN) {
            throw new BusinessException(ErrorCode.EVENT_NOT_OPEN);
        }

        Application existingApplication = applicationRepository.findByEventIdAndMemberId(eventId, member.getId())
                .orElse(null);

        if (existingApplication != null) {
            if (existingApplication.getStatus() != ApplicationStatus.CANCELED) {
                throw new BusinessException(ErrorCode.ALREADY_APPLIED);
            }

            existingApplication.updateStatus(ApplicationStatus.PENDING);
            return new ApplicationResponse(existingApplication);
        }

        Application application = Application.createApplication(event, member);
        return new ApplicationResponse(applicationRepository.save(application));
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getMyApplication(Long eventId, String email) {
        Member member = getMember(email);

        Application application = applicationRepository.findByEventIdAndMemberId(eventId, member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        return new ApplicationResponse(application);
    }

    @Transactional
    public void cancelMyApplication(Long eventId, String email) {
        Member member = getMember(email);

        Application application = applicationRepository.findByEventIdAndMemberId(eventId, member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_CANCELABLE);
        }

        application.cancel();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplications(Long eventId) {
        getEvent(eventId);//존재하지 않는 이벤트일 경우 빈 List -> EVENT_NOT_FOUND

        return applicationRepository.findByEventId(eventId).stream()
                .map(ApplicationResponse::new)
                .toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long eventId, Long applicationId, ApplicationStatusUpdateRequest request) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        if (!application.getEvent().getId().equals(event.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (request.getStatus() == ApplicationStatus.APPROVED
                && application.getStatus() != ApplicationStatus.APPROVED) {
            validateEventCapacity(event);
        }

        application.updateStatus(request.getStatus());
        return new ApplicationResponse(application);
    }

    private void validateEventCapacity(Event event) {
        Integer capacity = event.getCapacity();
        if (capacity == null) {
            return;
        }

        long approvedCount = applicationRepository.countByEventIdAndStatus(event.getId(), ApplicationStatus.APPROVED);
        if (approvedCount >= capacity) {
            throw new BusinessException(ErrorCode.EVENT_CAPACITY_EXCEEDED);
        }
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
