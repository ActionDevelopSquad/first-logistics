package com.firstlogistics.orderservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_ITEM_NOT_EXIST(HttpStatus.BAD_REQUEST, "ORD001", "주문 상품은 1개 이상이어야 합니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORD002", "허용되지 않은 주문 상태 변경입니다."),
    DELIVERY_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "ORD003", "이미 배송이 할당된 주문입니다."),
    INVALID_DELIVERY_ID(HttpStatus.BAD_REQUEST, "ORD004", "배송 할당을 위해서는 유효한 배송 ID가 필수입니다."),
    INVALID_MONEY_AMOUNT(HttpStatus.BAD_REQUEST, "ORD005", "금액은 null이거나 0보다 작을 수 없습니다."),
    ALREADY_CANCEL_REQUESTED(HttpStatus.BAD_REQUEST, "ORD006", "이미 취소 요청된 주문입니다."),
    CANNOT_REJECT_CANCEL(HttpStatus.BAD_REQUEST, "ORD007", "취소 요청 상태 주문이 아닙니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}