package com.firstlogistics.userservice.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode implements SuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "USER_001", "로그인 되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "USER_002", "로그아웃 되었습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "USER_003", "승인 대기중입니다."),
    ROLE_UPDATED(HttpStatus.OK, "USER_004", "권한이 변경되었습니다."),
    GET_USER(HttpStatus.OK, "USER_005", "회원이 조회되었습니다."),
    GET_USERS(HttpStatus.OK, "USER_006", "회원 목록이 조회되었습니다."),
    STATUS_UPDATED(HttpStatus.OK, "USER_007", "회원 상태가 변경되었습니다."),
    USER_UPDATED(HttpStatus.OK, "USER_008", "회원 정보가 변경되었습니다."),
    USER_DELETED(HttpStatus.OK, "USER_009", "회원 탈퇴 되었습니다."),
    TOKEN_REFRESHED(HttpStatus.OK, "USER_010", "토큰이 재발급 되었습니다."),
    KAFKA_FAILURE_REPUBLISHED(HttpStatus.OK, "USER_011", "Kafka 실패 이력이 재발행됐습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
