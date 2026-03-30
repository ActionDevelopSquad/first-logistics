package com.firstlogistics.userservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // 400 Error
    ALREADY_APPROVE(HttpStatus.BAD_REQUEST, "USER_001", "이미 승인된 사용자입니다."),
    ALREADY_REJECTED(HttpStatus.BAD_REQUEST, "USER_002", "이미 거부된 사용자입니다."),
    CAN_LOGIN_ONLY_APPROVE(HttpStatus.BAD_REQUEST, "USER_003", "승인된 사용자만 로그인할 수 있습니다."),

    // 404 Error
    ID_PASSWORD_NOT_MATCH(HttpStatus.NOT_FOUND, "USER_004", "아이디 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_005", "존재하지 않는 회원입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_006", "refresh token이 없습니다."),

    // 5xx Error
    AUTH_SERVER_REQUEST_ERROR(HttpStatus.BAD_GATEWAY, "AUTH_001", "인증 서버 요청 중 오류가 발생했습니다."),
    AUTH_SERVER_INTERNAL_ERROR(HttpStatus.BAD_GATEWAY, "AUTH_002", "인증 서버에 일시적인 오류가 발생했습니다."),
    AUTH_SERVER_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AUTH_003", "인증 서버와 통신할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
