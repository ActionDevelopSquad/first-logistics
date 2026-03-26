package com.firstlogistics.orderservice.domain.exception;

import common.exception.BaseException;

public class OrderException extends BaseException {

    public OrderException(OrderErrorCode errorCode) {
        super(errorCode);
    }
}