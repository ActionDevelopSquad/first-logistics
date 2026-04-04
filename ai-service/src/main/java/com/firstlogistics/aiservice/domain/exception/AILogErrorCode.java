package com.firstlogistics.aiservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AILogErrorCode implements ErrorCode {
    MESSAGE_NOT_EXIST(HttpStatus.BAD_REQUEST, "AI001", "메시지가 존재하지 않습니다."),
    MESSAGE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "AI002", "이미 메시지가 존재합니다."),
    CANNOT_UPDATE_STATUS(HttpStatus.BAD_REQUEST, "AI003", "상태 변경을 할 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI004", "서버 내부 오류가 발생했습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "AI005", "잘못된 파라미터입니다."),
    AILOG_NOT_FOUND(HttpStatus.NOT_FOUND, "AI006", "AI 로그가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
