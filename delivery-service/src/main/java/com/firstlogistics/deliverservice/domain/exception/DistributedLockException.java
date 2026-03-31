package com.firstlogistics.deliverservice.domain.exception;

import common.exception.BaseException;

public class DistributedLockException extends BaseException {

    public DistributedLockException(DeliveryErrorCode errorCode) {
        super(errorCode);
    }
}