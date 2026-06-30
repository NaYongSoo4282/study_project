package setuyeon.study.attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setuyeon.study.application.domain.Application;
import setuyeon.study.application.domain.ApplicationStatus;
import setuyeon.study.application.repository.ApplicationRepository;
import setuyeon.study.attendance.domain.Attendance;
import setuyeon.study.attendance.domain.AttendanceCode;
import setuyeon.study.attendance.dto.AttendanceCheckRequest;
import setuyeon.study.attendance.dto.AttendanceResponse;
import setuyeon.study.attendance.repository.AttendanceCodeRepository;
import setuyeon.study.attendance.repository.AttendanceRepository;
import setuyeon.study.event.domain.Event;
import setuyeon.study.event.repository.EventRepository;
import setuyeon.study.global.exception.BusinessException;
import setuyeon.study.global.exception.ErrorCode;
import setuyeon.study.member.domain.Member;
import setuyeon.study.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceCodeRepository attendanceCodeRepository;
    private final ApplicationRepository applicationRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public AttendanceResponse checkAttendance(Long eventId, AttendanceCheckRequest request, String email) {
        Event event = getEvent(eventId);
        Member member = getMember(email);

        Application application = applicationRepository.findByEventIdAndMemberId(eventId, member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_APPROVED);
        }

        if (attendanceRepository.existsByEventIdAndMemberId(eventId, member.getId())) {
            throw new BusinessException(ErrorCode.ALREADY_ATTENDED);
        }

        AttendanceCode attendanceCode = attendanceCodeRepository.findTopByEventIdOrderByIdDesc(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ATTENDANCE_CODE_NOT_FOUND));

        if (!attendanceCode.getCode().equals(request.getCode())) {
            throw new BusinessException(ErrorCode.INVALID_ATTENDANCE_CODE);
        }

        Attendance attendance = Attendance.createAttendance(event, member);
        return new AttendanceResponse(attendanceRepository.save(attendance));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendances(Long eventId) {
        return attendanceRepository.findByEventId(eventId).stream()
                .map(AttendanceResponse::new)
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
