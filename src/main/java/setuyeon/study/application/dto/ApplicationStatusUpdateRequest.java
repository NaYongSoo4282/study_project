package setuyeon.study.application.dto;

import lombok.Getter;
import setuyeon.study.application.domain.ApplicationStatus;

@Getter
public class ApplicationStatusUpdateRequest {
    private ApplicationStatus status;
}
