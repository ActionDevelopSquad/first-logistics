package com.firstlogistics.orderservice.domain.enums;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;

import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    PENDING,    // 주문 접수
    ACCEPTED,   // 주문 승인
    SHIPPING,   // 배송 중
    COMPLETED,  // 배송 완료
    CANCELLED;   // 주문 취소

    // 상태 전이 Map
    private static final Map<OrderStatus, Set<OrderStatus>> transitions = Map.of(
            PENDING, Set.of(ACCEPTED, CANCELLED),
            ACCEPTED, Set.of(SHIPPING, CANCELLED),
            SHIPPING, Set.of(COMPLETED),
            COMPLETED, Set.of(),
            CANCELLED, Set.of()
    );

    public void validateNext(OrderStatus next) {
        if (!transitions.getOrDefault(this, Set.of()).contains(next)) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
    }
}
