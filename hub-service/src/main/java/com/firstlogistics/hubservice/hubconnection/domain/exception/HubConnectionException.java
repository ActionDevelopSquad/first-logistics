package com.firstlogistics.hubservice.hubconnection.domain.exception;

import common.exception.BaseException;
import common.response.ErrorCode;

public class HubConnectionException extends BaseException {
    public HubConnectionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
