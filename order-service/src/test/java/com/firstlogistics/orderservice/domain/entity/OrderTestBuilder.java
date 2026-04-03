package com.firstlogistics.orderservice.domain.entity;

import com.firstlogistics.orderservice.domain.enums.OrderCancelType;
import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderTestBuilder {
    private UUID id = UUID.randomUUID();
    private UUID supplierCompanyId = UUID.randomUUID();
    private UUID supplierManagerId = UUID.randomUUID();
    private UUID receiverCompanyId = UUID.randomUUID();
    private UUID receiverManagerId = UUID.randomUUID();
    private UUID deliveryId = null;
    private String roadAddress = "테스트 도로명 주소";
    private String detailAddress = "상세 주소 101호";
    private Long totalAmount = 10000L;
    private LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
    private String requestMemo = "빠른 배송 바랍니다.";
    private OrderStatus status = OrderStatus.PENDING;
    private OrderStatus previousStatus = null;
    private OrderCancelType cancelType = null;
    private LocalDateTime orderedAt = LocalDateTime.now();
    private List<OrderItem> orderItems = new ArrayList<>();
    private Long version = 0L;

    public static OrderTestBuilder builder() {
        return new OrderTestBuilder();
    }

    public OrderTestBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public OrderTestBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderTestBuilder deliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
        return this;
    }

    public OrderTestBuilder previousStatus(OrderStatus previousStatus) {
        this.previousStatus = previousStatus;
        return this;
    }

    public Order build() {
        return Order.reconstitute(
                id, supplierCompanyId, supplierManagerId, receiverCompanyId, receiverManagerId,
                deliveryId, roadAddress, detailAddress, totalAmount, dueDate,
                requestMemo, status, previousStatus, cancelType, orderedAt, orderItems, version
        );
    }
}