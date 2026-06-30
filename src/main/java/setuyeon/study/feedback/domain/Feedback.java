package setuyeon.study.feedback.domain;

import jakarta.persistence.*;
import lombok.Getter;
import setuyeon.study.event.domain.Event;
import setuyeon.study.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Getter
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    protected Feedback() {
    }

    public static Feedback createFeedback(Event event, Member member, String content) {
        Feedback feedback = new Feedback();
        feedback.event = event;
        feedback.member = member;
        feedback.content = content;
        feedback.createdAt = LocalDateTime.now();
        return feedback;
    }
}
