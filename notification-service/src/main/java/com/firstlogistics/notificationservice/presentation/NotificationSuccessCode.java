package com.firstlogistics.notificationservice.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationSuccessCode implements SuccessCode {

    NOTIFICATION_CREATED(HttpStatus.CREATED, "N_S001", "알림이 생성되었습니다."),
    NOTIFICATION_FOUND(HttpStatus.OK, "N_S002", "알림 상세 정보가 조회되었습니다."),
    NOTIFICATION_LIST_FOUND(HttpStatus.OK, "N_S003", "알림 목록이 조회되었습니다."),
    NOTIFICATION_STATUS_UPDATED(HttpStatus.OK, "N_S004", "알림 상태가 변경되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}