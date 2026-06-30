package setuyeon.study.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Invalid request."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access is denied."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Internal server error."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "Email is already registered."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "Member was not found."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "Email or password is incorrect."),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT_NOT_FOUND", "Event was not found."),
    EVENT_NOT_OPEN(HttpStatus.BAD_REQUEST, "EVENT_NOT_OPEN", "Only open events can receive applications."),
    EVENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "EVENT_NOT_COMPLETED", "Feedback is allowed only for completed events."),
    EVENT_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "EVENT_CAPACITY_EXCEEDED", "Event capacity has been reached."),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "ALREADY_APPLIED", "Already applied to this event."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION_NOT_FOUND", "Application was not found."),
    APPLICATION_NOT_CANCELABLE(HttpStatus.BAD_REQUEST, "APPLICATION_NOT_CANCELABLE", "Only pending applications can be canceled."),
    APPLICATION_NOT_APPROVED(HttpStatus.FORBIDDEN, "APPLICATION_NOT_APPROVED", "Only approved applications can attend."),
    ALREADY_ATTENDED(HttpStatus.CONFLICT, "ALREADY_ATTENDED", "Already attended this event."),
    ATTENDANCE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATTENDANCE_CODE_NOT_FOUND", "Attendance code was not found."),
    INVALID_ATTENDANCE_CODE(HttpStatus.BAD_REQUEST, "INVALID_ATTENDANCE_CODE", "Attendance code is invalid."),
    FEEDBACK_ATTENDANCE_REQUIRED(HttpStatus.FORBIDDEN, "FEEDBACK_ATTENDANCE_REQUIRED", "Only attended members can submit feedback."),
    ALREADY_FEEDBACK_SUBMITTED(HttpStatus.CONFLICT, "ALREADY_FEEDBACK_SUBMITTED", "Feedback has already been submitted for this event.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
