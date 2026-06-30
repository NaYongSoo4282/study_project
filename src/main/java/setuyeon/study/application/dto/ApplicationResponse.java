package setuyeon.study.application.dto;

import lombok.Getter;
import setuyeon.study.application.domain.Application;
import setuyeon.study.application.domain.ApplicationStatus;

import java.time.LocalDateTime;

@Getter
public class ApplicationResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public ApplicationResponse(Application application) {
        this.id = application.getId();
        this.eventId = application.getEvent().getId();
        this.eventTitle = application.getEvent().getTitle();
        this.memberId = application.getMember().getId();
        this.memberName = application.getMember().getName();
        this.memberEmail = application.getMember().getEmail();
        this.status = application.getStatus();
        this.appliedAt = application.getAppliedAt();
    }
}
