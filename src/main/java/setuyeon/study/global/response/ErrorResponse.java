package setuyeon.study.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import setuyeon.study.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String code;
    private String message;

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message
        );
    }
}
