package com.firstlogistics.sampleservice.domain.item.exception;

import common.exception.BaseException;

public class ItemException extends BaseException {

    public ItemException(ItemErrorCode errorCode) {
        super(errorCode);
    }
}
