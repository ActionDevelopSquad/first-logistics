package com.firstlogistics.orderservice.domain.entity;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.vo.Money;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.domain.vo.Receiver;
import com.firstlogistics.orderservice.domain.vo.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    private OrderId id;
    private Supplier supplier;
    private Receiver receiver;
    private UUID deliveryId;
    private Money totalAmount;
    private LocalDateTime dueDate;
    private String requestMemo;
    private OrderStatus status;
    private OrderStatus previousStatus;

    @Getter(AccessLevel.NONE)
    private List<OrderItem> orderItems;

    public static Order create(
            UUID supplierCompanyId,
            UUID supplierManagerId,
            UUID receiverCompanyId,
            UUID receiverManagerId,
            LocalDateTime dueDate,
            String requestMemo,
            List<OrderItem> items // 추후 DTO로 수정
    ) {
        Order order = new Order(
                OrderId.of(),
                Supplier.of(supplierCompanyId, supplierManagerId),
                Receiver.of(receiverCompanyId, receiverManagerId),
                null,
                Money.of(0L),
                dueDate,
                requestMemo,
                OrderStatus.PENDING,
                null,
                new ArrayList<>()
        );

        order.initOrderItems(items);
        order.calculateTotalAmount();

        return order;
    }

    // 외부에서 리스트 수정 못하도록 읽기 전용으로 반환
    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    private void initOrderItems(List<OrderItem> orderItems) {
        // 주문 상세 존재 여부 체크
        if (orderItems == null || orderItems.isEmpty()) {
            throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_EXIST);
        }
        if (orderItems.stream().anyMatch(Objects::isNull)) {
            throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_EXIST);
        }

        // 주문 가능한 상품인지 체크?

        this.orderItems = new ArrayList<>();
        orderItems.forEach(this::addOrderItem);
    }

    private void addOrderItem(OrderItem item) {
        // 개별 검증 로직 추가
        orderItems.add(OrderItem.create(
                this.id,
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity()
        ));
    }

    private void calculateTotalAmount() {
        this.totalAmount = Money.of(orderItems.stream()
                .mapToLong(item -> item.getSubTotal().amount())
                .sum());
    }

    public void reserve(boolean isSuccess) {
        OrderStatus resultStatus = isSuccess ? OrderStatus.RESERVED : OrderStatus.CANCELLED;

        // 취소 요청 상태에서는 이전 상태 업데이트
        if (this.status == OrderStatus.CANCEL_REQUESTED) {
            this.previousStatus.validateNext(resultStatus);
            this.previousStatus = resultStatus;
            return;
        }

        this.status.validateNext(resultStatus);
        this.status = resultStatus;
    }

    public void accept() {
        this.status.validateNext(OrderStatus.ACCEPTED);
        this.status = OrderStatus.ACCEPTED;
    }

    public void assignDelivery(UUID deliveryId) {
        if (deliveryId == null) {
            throw new OrderException(OrderErrorCode.INVALID_DELIVERY_ID);
        }

        // 이미 배송이 할당된 경우
        if (this.deliveryId != null) {
            throw new OrderException(OrderErrorCode.DELIVERY_ALREADY_ASSIGNED);
        }

        this.status.validateNext(OrderStatus.READY);
        this.deliveryId = deliveryId;
        this.status = OrderStatus.READY;
    }

    public void startShipping() {
        this.status.validateNext(OrderStatus.SHIPPING);
        this.status = OrderStatus.SHIPPING;
    }

    public void complete() {
        this.status.validateNext(OrderStatus.COMPLETED);
        this.status = OrderStatus.COMPLETED;
    }

    // 주문 취소 / 거절 / 취소 요청 승인 (나중에 필요하면 분리)
    public void cancel() {
        this.status.validateNext(OrderStatus.CANCELLED);
        this.status = OrderStatus.CANCELLED;
        this.previousStatus = null; // 취소 요청이었다면 이전 상태 초기화
    }

    public void requestCancel() {
        this.status.validateNext(OrderStatus.CANCEL_REQUESTED);
        this.previousStatus = this.status;
        this.status = OrderStatus.CANCEL_REQUESTED;
    }

    public void rejectCancelRequest() {
        this.status.validateNext(this.previousStatus);
        this.status = this.previousStatus; // 이전 상태 복구
        this.previousStatus = null;
    }

}
