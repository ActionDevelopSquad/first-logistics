package com.firstlogistics.notificationservice.ailog.domain.exception;

import common.exception.BaseException;

public class AILogException extends BaseException {
    public AILogException(AILogErrorCode message) {
        super(message);
    }
}
