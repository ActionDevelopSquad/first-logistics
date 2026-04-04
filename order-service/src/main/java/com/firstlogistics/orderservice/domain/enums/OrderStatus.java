package com.firstlogistics.orderservice.domain.enums;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;

import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    PENDING,            // 주문 접수
    RESERVED,           // 주문 예약 (재고 예약 완료)
    ACCEPTED,           // 주문 승인 (관리자 승인)
    READY,              // 배송 준비 (배송 ID 할당)
    SHIPPING,           // 배송 중
    COMPLETED,          // 배송 완료
    CANCEL_REQUESTED,   // 주문 취소 요청
    CANCELLED;          // 주문 취소

    public static OrderStatus from(String status) {
        if (status == null || status.isBlank()) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS_CHANGE);
        }

        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS_CHANGE);
        }
    }

    // 상태 전이 Map
    private static final Map<OrderStatus, Set<OrderStatus>> transitions = Map.of(
            PENDING, Set.of(RESERVED, CANCEL_REQUESTED, CANCELLED),
            RESERVED, Set.of(ACCEPTED, CANCEL_REQUESTED, CANCELLED),
            ACCEPTED, Set.of(READY),
            READY, Set.of(SHIPPING),
            SHIPPING, Set.of(COMPLETED),
            COMPLETED, Set.of(),
            CANCEL_REQUESTED, Set.of(CANCELLED, PENDING, RESERVED),
            CANCELLED, Set.of()
    );

    public void validateNext(OrderStatus next) {
        if (this == next) return;
        if (!transitions.getOrDefault(this, Set.of()).contains(next)) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS_CHANGE);
        }
    }
}
