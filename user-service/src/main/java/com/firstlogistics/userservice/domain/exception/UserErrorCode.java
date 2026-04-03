package com.firstlogistics.userservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // 400 BAD REQUEST
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "USER_400_001", "이미 존재하는 아이디입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "USER_400_002", "이미 존재하는 이메일입니다."),
    CAN_LOGIN_ONLY_APPROVE(HttpStatus.BAD_REQUEST, "USER_400_003", "승인된 사용자만 로그인할 수 있습니다."),
    ALREADY_APPROVE(HttpStatus.BAD_REQUEST, "USER_400_004", "이미 승인된 사용자입니다."),
    ALREADY_REJECTED(HttpStatus.BAD_REQUEST, "USER_400_005", "이미 거부된 사용자입니다."),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "USER_400_006", "탈퇴 회원 정보는 수정이 불가능합니다."),
    SAME_ROLE_SELECTED(HttpStatus.BAD_REQUEST, "USER_400_007", "현재 권한과 수정된 권한이 동일합니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_400_008", "refresh token이 없습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "USER_401_001", "인증이 필요합니다."),

    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "USER_403_001", "권한이 필요합니다."),

    // 404 NOT FOUND,
    ID_PASSWORD_NOT_MATCH(HttpStatus.NOT_FOUND, "USER_404_001", "아이디 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_002", "존재하지 않는 회원입니다."),
    ORGANIZATION_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_003", "존재하지 않는 소속 ID 입니다."),

    // 5xx SERVER ERROR
    AUTH_SERVER_REQUEST_ERROR(HttpStatus.BAD_GATEWAY, "AUTH_502_001", "인증 서버 요청 중 오류가 발생했습니다."),
    AUTH_SERVER_INTERNAL_ERROR(HttpStatus.BAD_GATEWAY, "AUTH_502_002", "인증 서버에 일시적인 오류가 발생했습니다."),
    AUTH_SERVER_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AUTH_503_003", "인증 서버와 통신할 수 없습니다."),
    FEIGN_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "AUTH_503_004", "해당 서비스를 이용할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
