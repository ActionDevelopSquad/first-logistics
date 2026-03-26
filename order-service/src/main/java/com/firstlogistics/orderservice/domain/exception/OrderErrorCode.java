package com.firstlogistics.orderservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_ITEM_NOT_EXIST(HttpStatus.BAD_REQUEST, "ORD_001", "주문 상품은 1개 이상이어야 합니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORD_002", "허용되지 않은 주문 상태 변경입니다."),
    DELIVERY_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "ORD_003", "이미 배송이 할당된 주문입니다."),
    INVALID_ORDER_STATUS_FOR_DELIVERY(HttpStatus.BAD_REQUEST, "ORD_004", "배송은 승인된 주문에서만 가능합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}