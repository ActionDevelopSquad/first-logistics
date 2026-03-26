package com.firstlogistics.sampleservice.domain.exception;

import common.exception.BaseException;

public class ItemException extends BaseException {

    public ItemException(ItemErrorCode errorCode) {
        super(errorCode);
    }
}
