package setuyeon.study.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    String message;
    int status;
    T data;

    protected ApiResponse(int status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }
}
