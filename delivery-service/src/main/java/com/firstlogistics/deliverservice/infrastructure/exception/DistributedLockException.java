package com.firstlogistics.deliverservice.infrastructure.exception;

public class DistributedLockException extends InfraException {

    public DistributedLockException(InfraErrorCode errorCode) {
        super(errorCode);
    }
}
