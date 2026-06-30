package setuyeon.study.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import setuyeon.study.application.domain.Application;
import setuyeon.study.application.domain.ApplicationStatus;
import setuyeon.study.application.repository.ApplicationRepository;
import setuyeon.study.attendance.domain.Attendance;
import setuyeon.study.attendance.domain.AttendanceCode;
import setuyeon.study.attendance.repository.AttendanceCodeRepository;
import setuyeon.study.attendance.repository.AttendanceRepository;
import setuyeon.study.event.domain.Event;
import setuyeon.study.event.domain.EventStatus;
import setuyeon.study.event.repository.EventRepository;
import setuyeon.study.feedback.domain.Feedback;
import setuyeon.study.feedback.repository.FeedbackRepository;
import setuyeon.study.member.domain.Member;
import setuyeon.study.member.domain.Role;
import setuyeon.study.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "password123";

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final ApplicationRepository applicationRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceCodeRepository attendanceCodeRepository;
    private final FeedbackRepository feedbackRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            return;
        }

        Member admin = Member.createMember(
                "Demo Admin",
                passwordEncoder.encode(DEMO_PASSWORD),
                "admin@example.com",
                Role.ADMIN
        );
        memberRepository.save(admin);

        List<Member> members = createMembers();
        memberRepository.saveAll(members);

        Event openEvent = createEvent(
                "Spring Security JWT Study",
                "JWT authentication and authorization practice session.",
                "Room A",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(3).plusHours(2),
                8,
                EventStatus.OPEN
        );
        Event completedEvent = createEvent(
                "JPA Transaction Workshop",
                "Transaction, lock, and JPA relationship demo.",
                "Room B",
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now().minusDays(7).plusHours(2),
                10,
                EventStatus.COMPLETED
        );
        Event draftEvent = createEvent(
                "Docker Deployment Rehearsal",
                "Container deployment practice.",
                "Online",
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(10).plusHours(2),
                12,
                EventStatus.DRAFT
        );
        eventRepository.saveAll(List.of(openEvent, completedEvent, draftEvent));

        attendanceCodeRepository.save(AttendanceCode.createAttendanceCode(openEvent, "123456"));
        attendanceCodeRepository.save(AttendanceCode.createAttendanceCode(completedEvent, "654321"));

        seedOpenEventApplications(openEvent, members);
        seedCompletedEventHistory(completedEvent, members);
    }

    private List<Member> createMembers() {
        String encodedPassword = passwordEncoder.encode(DEMO_PASSWORD);
        List<Member> members = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            members.add(Member.createMember(
                    "Demo Member " + i,
                    encodedPassword,
                    "member" + i + "@example.com",
                    Role.MEMBER
            ));
        }

        return members;
    }

    private Event createEvent(String title, String description, String location,
                              LocalDateTime startAt, LocalDateTime endAt,
                              Integer capacity, EventStatus status) {
        Event event = Event.createEvent(title, description, location, startAt, endAt, capacity);
        event.updateStatus(status);
        return event;
    }

    private void seedOpenEventApplications(Event event, List<Member> members) {
        List<Application> applications = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            Application application = Application.createApplication(event, members.get(i));

            if (i < 6) {
                application.updateStatus(ApplicationStatus.APPROVED);
            } else if (i < 8) {
                application.updateStatus(ApplicationStatus.PENDING);
            } else {
                application.updateStatus(ApplicationStatus.REJECTED);
            }

            applications.add(application);
        }

        applicationRepository.saveAll(applications);
    }

    private void seedCompletedEventHistory(Event event, List<Member> members) {
        List<Application> applications = new ArrayList<>();
        List<Attendance> attendances = new ArrayList<>();
        List<Feedback> feedbacks = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            Application application = Application.createApplication(event, members.get(i));

            if (i < 7) {
                application.updateStatus(ApplicationStatus.APPROVED);
                attendances.add(Attendance.createAttendance(event, members.get(i)));
            } else {
                application.updateStatus(ApplicationStatus.CANCELED);
            }

            applications.add(application);
        }

        for (int i = 0; i < 5; i++) {
            feedbacks.add(Feedback.createFeedback(
                    event,
                    members.get(i),
                    "Helpful demo feedback from member " + (i + 1) + "."
            ));
        }

        applicationRepository.saveAll(applications);
        attendanceRepository.saveAll(attendances);
        feedbackRepository.saveAll(feedbacks);
    }
}
