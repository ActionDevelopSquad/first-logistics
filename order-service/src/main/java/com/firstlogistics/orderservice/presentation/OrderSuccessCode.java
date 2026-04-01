package com.firstlogistics.orderservice.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderSuccessCode implements SuccessCode {

    ORDER_OK(HttpStatus.OK, "ORD200", "주문 조회를 성공하였습니다."),
    ORDER_CREATED(HttpStatus.CREATED, "ORD201", "주문이 생성 완료되었습니다."),
    ORDER_STATUS_UPDATED(HttpStatus.OK, "ORD202", "주문 상태가 변경되었습니다."),
    ORDER_DELETED(HttpStatus.OK, "ORD203", "주문이 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}