package com.firstlogistics.productservice.product.domain.exception;

import common.exception.BaseException;
import common.response.ErrorCode;

public class ProductException extends BaseException {

    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}
