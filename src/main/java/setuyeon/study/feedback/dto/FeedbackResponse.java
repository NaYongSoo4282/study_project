package setuyeon.study.feedback.dto;

import lombok.Getter;
import setuyeon.study.feedback.domain.Feedback;

import java.time.LocalDateTime;

@Getter
public class FeedbackResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long memberId;
    private String memberName;
    private String content;
    private LocalDateTime createdAt;

    public FeedbackResponse(Feedback feedback) {
        this.id = feedback.getId();
        this.eventId = feedback.getEvent().getId();
        this.eventTitle = feedback.getEvent().getTitle();
        this.memberId = feedback.getMember().getId();
        this.memberName = feedback.getMember().getName();
        this.content = feedback.getContent();
        this.createdAt = feedback.getCreatedAt();
    }
}
