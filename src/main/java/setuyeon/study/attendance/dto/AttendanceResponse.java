package setuyeon.study.attendance.dto;

import lombok.Getter;
import setuyeon.study.attendance.domain.Attendance;

import java.time.LocalDateTime;

@Getter
public class AttendanceResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private LocalDateTime attendedAt;

    public AttendanceResponse(Attendance attendance) {
        this.id = attendance.getId();
        this.eventId = attendance.getEvent().getId();
        this.eventTitle = attendance.getEvent().getTitle();
        this.memberId = attendance.getMember().getId();
        this.memberName = attendance.getMember().getName();
        this.memberEmail = attendance.getMember().getEmail();
        this.attendedAt = attendance.getAttendedAt();
    }
}
