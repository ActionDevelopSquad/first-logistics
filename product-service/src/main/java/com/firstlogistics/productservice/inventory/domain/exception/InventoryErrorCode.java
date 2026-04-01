package com.firstlogistics.productservice.inventory.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum InventoryErrorCode implements ErrorCode {
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "INV001", "수량이 유효하지 않습니다."),
    INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "INV002", "상품 ID가 유효하지 않습니다."),
    INSUFFICIENT_AVAILABLE_QUANTITY(HttpStatus.BAD_REQUEST, "INV003", "사용 가능한 수량이 부족합니다."),
    INSUFFICIENT_RESERVED_QUANTITY(HttpStatus.BAD_REQUEST, "INV004", "예약된 수량이 부족합니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
