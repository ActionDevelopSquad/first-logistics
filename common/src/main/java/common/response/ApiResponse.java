package common.response;

import java.time.LocalDateTime;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final HttpStatus status;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    private ApiResponse(SuccessCode successCode, T data) {
        this.code = successCode.getCode();
        this.status = successCode.getStatus();
        this.message = successCode.getMessage();
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    private ApiResponse(ErrorCode errorCode, T data) {
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    private ApiResponse(ErrorCode errorCode, String customMessage, T data) {
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.message = customMessage;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return new ApiResponse<>(successCode, data);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return new ApiResponse<>(errorCode, customMessage, null);
    }
}