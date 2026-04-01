package com.firstlogistics.userservice.domain.exception;

import common.exception.BaseException;

public class UserException extends BaseException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
