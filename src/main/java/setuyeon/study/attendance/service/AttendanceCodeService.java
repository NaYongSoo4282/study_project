package setuyeon.study.attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setuyeon.study.attendance.domain.AttendanceCode;
import setuyeon.study.attendance.dto.AttendanceCodeResponse;
import setuyeon.study.attendance.repository.AttendanceCodeRepository;
import setuyeon.study.event.domain.Event;
import setuyeon.study.event.repository.EventRepository;
import setuyeon.study.global.exception.BusinessException;
import setuyeon.study.global.exception.ErrorCode;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AttendanceCodeService {

    private final AttendanceCodeRepository attendanceCodeRepository;
    private final EventRepository eventRepository;

    @Transactional
    public AttendanceCodeResponse createCode(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        AttendanceCode attendanceCode = AttendanceCode.createAttendanceCode(event, code);
        return new AttendanceCodeResponse(attendanceCodeRepository.save(attendanceCode));
    }
}
