package com.firstlogistics.notificationservice.domain.exception;

import common.exception.BaseException;

public class NotificationException extends BaseException {
    public NotificationException(NotificationErrorCode errorCode) {
        super(errorCode);
    }
}
