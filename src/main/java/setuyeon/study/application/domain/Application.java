package setuyeon.study.application.domain;

import jakarta.persistence.*;
import lombok.Getter;
import setuyeon.study.event.domain.Event;
import setuyeon.study.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Getter
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime appliedAt;

    protected Application() {
    }

    public static Application createApplication(Event event, Member member) {
        Application application = new Application();
        application.event = event;
        application.member = member;
        application.status = ApplicationStatus.PENDING;
        application.appliedAt = LocalDateTime.now();
        return application;
    }

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void cancel() {
        this.status = ApplicationStatus.CANCELED;
    }
}
