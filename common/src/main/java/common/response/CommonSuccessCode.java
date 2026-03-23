package common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonSuccessCode implements SuccessCode {

    OK(HttpStatus.OK, "COMMON_S001", "성공하였습니다."),
    CREATED(HttpStatus.CREATED, "COMMON_S002", "생성되었습니다."),
    DELETED(HttpStatus.OK, "COMMON_S003", "삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}