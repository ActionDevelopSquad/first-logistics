package com.firstlogistics.notificationservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AILogErrorCode implements ErrorCode {
    MESSAGE_NOT_EXIST(HttpStatus.BAD_REQUEST, "AI001", "메시지가 존재하지 않습니다."),
    MESSAGE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "AI002", "이미 메시지가 존재합니다."),
    CANNOT_UPDATE_STATUS(HttpStatus.BAD_REQUEST, "AI003", "상태 변경을 할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
