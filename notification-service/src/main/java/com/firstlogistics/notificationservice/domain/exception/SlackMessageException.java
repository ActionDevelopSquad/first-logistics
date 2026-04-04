package com.firstlogistics.notificationservice.domain.exception;

import common.exception.BaseException;

public class SlackMessageException extends BaseException {
    public SlackMessageException(SlackMessageErrorCode errorCode) {
        super(errorCode);
    }
}
