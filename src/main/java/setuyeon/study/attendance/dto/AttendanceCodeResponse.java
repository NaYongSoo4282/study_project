package setuyeon.study.attendance.dto;

import lombok.Getter;
import setuyeon.study.attendance.domain.AttendanceCode;

import java.time.LocalDateTime;

@Getter
public class AttendanceCodeResponse {
    private Long id;
    private Long eventId;
    private String code;
    private LocalDateTime createdAt;

    public AttendanceCodeResponse(AttendanceCode attendanceCode) {
        this.id = attendanceCode.getId();
        this.eventId = attendanceCode.getEvent().getId();
        this.code = attendanceCode.getCode();
        this.createdAt = attendanceCode.getCreatedAt();
    }
}
