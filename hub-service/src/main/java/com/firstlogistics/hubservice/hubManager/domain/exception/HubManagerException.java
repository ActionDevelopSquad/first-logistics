package com.firstlogistics.hubservice.hubManager.domain.exception;

import common.exception.BaseException;
import common.response.ErrorCode;

public class HubManagerException extends BaseException {
    public HubManagerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
