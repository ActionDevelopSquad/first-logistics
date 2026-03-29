package com.firstlogistics.companyservice.domain.exception;

import common.exception.BaseException;
import common.response.ErrorCode;

public class CompanyException extends BaseException {

    public CompanyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
