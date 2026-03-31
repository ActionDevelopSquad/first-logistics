package com.firstlogistics.userservice.presentation.code;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode implements SuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "USER_001", "로그인 되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "USER_002", "로그아웃 되었습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "USER_003", "회원가입 되었습니다."),
    ROLE_UPDATED(HttpStatus.OK, "USER_004", "권한이 변경되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
