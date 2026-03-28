package com.firstlogistics.deliverservice.domain.exception;

import common.exception.BaseException;

public class DeliveryException extends BaseException {

    public DeliveryException(DeliveryErrorCode errorCode) {
        super(errorCode);
    }
}
