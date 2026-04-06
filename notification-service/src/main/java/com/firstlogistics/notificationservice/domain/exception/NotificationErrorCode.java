package com.firstlogistics.notificationservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    CANNOT_UPDATE_STATUS(HttpStatus.BAD_REQUEST, "N001", "상태 변경을 할 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "N002", "서버에 문제가 발생했습니다."),
    SLACK_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "N003", "슬랙 메시지 전송에 실패했습니다."),
    SLACK_API_COMMUNICATION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "N004", "슬랙 외부 API와의 통신이 원활하지 않습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N005", "해당 알림 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
