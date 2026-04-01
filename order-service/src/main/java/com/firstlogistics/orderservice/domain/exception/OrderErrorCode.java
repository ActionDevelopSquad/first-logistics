package com.firstlogistics.orderservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    // 404 NOT_FOUND
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD001", "존재하지 않는 주문입니다."),

    // 400 BAD_REQUEST
    ORDER_ITEM_NOT_EXIST(HttpStatus.BAD_REQUEST, "ORD002", "주문 상품은 1개 이상이어야 합니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORD003", "허용되지 않은 주문 상태 변경입니다."),
    DELIVERY_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "ORD004", "이미 배송이 할당된 주문입니다."),
    INVALID_DELIVERY_ID(HttpStatus.BAD_REQUEST, "ORD005", "배송 할당을 위해서는 유효한 배송 ID가 필수입니다."),
    INVALID_MONEY_AMOUNT(HttpStatus.BAD_REQUEST, "ORD006", "금액은 null이거나 0보다 작을 수 없습니다."),
    ALREADY_CANCEL_REQUESTED(HttpStatus.BAD_REQUEST, "ORD007", "이미 취소 요청된 주문입니다."),
    CANNOT_REJECT_CANCEL(HttpStatus.BAD_REQUEST, "ORD008", "취소 요청 상태 주문이 아닙니다."),
    INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "ORD009", "유효한 상품 ID가 아닙니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "ORD0010", "주문 수량은 최소 1개 이상이어야 합니다."),
    INVALID_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "ORD011", "유효한 상품 이름이 아닙니다."),
    INVALID_DUE_DATE(HttpStatus.BAD_REQUEST, "ORD012", "유효하지 않은 납품기한입니다."),
    DELIVERY_ADDRESS_REQUIRED(HttpStatus.BAD_REQUEST, "ORD013", "배송 주소는 필수입니다."),
    SUPPLIER_COMPANY_REQUIRED(HttpStatus.BAD_REQUEST, "ORD014", "공급 업체 ID는 필수 항목입니다."),
    SUPPLIER_MANAGER_REQUIRED(HttpStatus.BAD_REQUEST, "ORD015", "공급 담당자 ID는 필수 항목입니다."),
    RECEIVER_COMPANY_REQUIRED(HttpStatus.BAD_REQUEST, "ORD016", "수신 업체 ID는 필수 항목입니다."),
    RECEIVER_MANAGER_REQUIRED(HttpStatus.BAD_REQUEST, "ORD017", "수신 담당자 ID는 필수 항목입니다."),
    INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, "ORD018", "유효하지 않은 주문 ID 입니다.");;

    private final HttpStatus status;
    private final String code;
    private final String message;
}