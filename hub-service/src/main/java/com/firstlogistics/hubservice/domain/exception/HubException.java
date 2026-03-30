package com.firstlogistics.hubservice.domain.exception;

import common.exception.BaseException;
import common.response.ErrorCode;

public class HubException extends BaseException {
    public HubException(ErrorCode errorCode) {
        super(errorCode);
    }
}
