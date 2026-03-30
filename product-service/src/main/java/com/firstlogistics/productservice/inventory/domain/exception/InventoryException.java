package com.firstlogistics.productservice.inventory.domain.exception;

import common.exception.BaseException;
import common.response.ErrorCode;

public class InventoryException extends BaseException {

    public InventoryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
