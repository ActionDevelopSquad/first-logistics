package com.firstlogistics.productservice.product.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProductErrorCode implements ErrorCode {
    INVALID_MONEY(HttpStatus.BAD_REQUEST, "PRO001", "금액이 유효하지 않습니다."),
    INVALID_COMPANY_ID(HttpStatus.BAD_REQUEST, "PRO002", "회사 ID가 유효하지 않습니다."),
    INVALID_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "PRO003", "상품 이름이 유효하지 않습니다."),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "PRO004", "상품 상태가 유효하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
