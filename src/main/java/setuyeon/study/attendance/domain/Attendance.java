package setuyeon.study.attendance.domain;

import jakarta.persistence.*;
import lombok.Getter;
import setuyeon.study.event.domain.Event;
import setuyeon.study.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Getter
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private LocalDateTime attendedAt;

    protected Attendance() {
    }

    public static Attendance createAttendance(Event event, Member member) {
        Attendance attendance = new Attendance();
        attendance.event = event;
        attendance.member = member;
        attendance.attendedAt = LocalDateTime.now();
        return attendance;
    }
}
