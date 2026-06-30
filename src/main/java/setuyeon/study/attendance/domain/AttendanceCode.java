package setuyeon.study.attendance.domain;

import jakarta.persistence.*;
import lombok.Getter;
import setuyeon.study.event.domain.Event;

import java.time.LocalDateTime;

@Entity
@Getter
public class AttendanceCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String code;

    private LocalDateTime createdAt;

    protected AttendanceCode() {
    }

    public static AttendanceCode createAttendanceCode(Event event, String code) {
        AttendanceCode attendanceCode = new AttendanceCode();
        attendanceCode.event = event;
        attendanceCode.code = code;
        attendanceCode.createdAt = LocalDateTime.now();
        return attendanceCode;
    }
}
