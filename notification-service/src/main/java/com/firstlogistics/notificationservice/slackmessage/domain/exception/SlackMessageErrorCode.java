package com.firstlogistics.notificationservice.slackmessage.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SlackMessageErrorCode implements ErrorCode {
    CANNOT_UPDATE_STATUS(HttpStatus.BAD_REQUEST, "SM001", "상태 변경을 할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
