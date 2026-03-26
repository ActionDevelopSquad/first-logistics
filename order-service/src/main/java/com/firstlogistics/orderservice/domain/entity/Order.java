package com.firstlogistics.orderservice.domain.entity;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.vo.Money;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.domain.vo.Receiver;
import com.firstlogistics.orderservice.domain.vo.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    private OrderId id;
    private Supplier supplier;
    private Receiver receiver;
    private UUID deliveryId;
    private OrderStatus status;
    private Money totalAmount;
    private LocalDateTime dueDate;
    private String requestMemo;
    private List<OrderItem> orderItems;

    public static Order create(
            UUID supplierCompanyId,
            UUID supplierManagerId,
            UUID receiverCompanyId,
            UUID receiverManagerId,
            LocalDateTime dueDate,
            String requestMemo,
            List<OrderItem> items
    ) {
        Order order = new Order();
        order.id = OrderId.of();
        order.supplier = Supplier.of(supplierCompanyId, supplierManagerId);
        order.receiver = Receiver.of(receiverCompanyId, receiverManagerId);
        order.dueDate = dueDate;
        order.requestMemo = requestMemo;
        order.status = OrderStatus.PENDING;

        order.setOrderItems(items);
        order.calculateTotalAmount();

        return order;
    }

    private void setOrderItems(List<OrderItem> orderItems) {
        // 주문 상세 존재 여부 체크
        if (orderItems == null || orderItems.isEmpty()) {
            throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_EXIST);
        }

        // 주문 가능한 상품인지 체크?

        this.orderItems = new ArrayList<>();
        orderItems.forEach(this::addOrderItem);
    }

    private void addOrderItem(OrderItem item) {
        // 개별 검증 로직 추가
        orderItems.add(item);
    }

    private void calculateTotalAmount() {
        this.totalAmount = Money.of(orderItems.stream()
                .mapToLong(x -> x.getSubTotal().getAmount())
                .sum());
    }

    public void accept() {
        this.status.validateNext(OrderStatus.ACCEPTED);
        this.status = OrderStatus.ACCEPTED;
    }

    public void assignDelivery(UUID deliveryId) {
        // 이미 배송이 할당된 경우
        if (this.deliveryId != null) {
            throw new OrderException(OrderErrorCode.DELIVERY_ALREADY_ASSIGNED);
        }

        // 주문 승인 상태에서만 배송 할당 가능
        if (!status.equals(OrderStatus.ACCEPTED)) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS_FOR_DELIVERY);
        }

        this.deliveryId = deliveryId;
    }

    public void startShipping() {
        this.status.validateNext(OrderStatus.SHIPPING);
        this.status = OrderStatus.SHIPPING;
    }

    public void complete() {
        this.status.validateNext(OrderStatus.COMPLETED);
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status.validateNext(OrderStatus.CANCELLED);
        this.status = OrderStatus.CANCELLED;
    }
}
