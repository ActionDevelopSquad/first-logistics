package com.firstlogistics.notificationservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    CANNOT_UPDATE_STATUS(HttpStatus.BAD_REQUEST, "N001", "상태 변경을 할 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "N002", "서버에 문제가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
