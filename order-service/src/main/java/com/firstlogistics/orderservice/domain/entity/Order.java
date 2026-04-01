package com.firstlogistics.orderservice.domain.entity;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.event.OrderEvents;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.vo.Address;
import com.firstlogistics.orderservice.domain.vo.Money;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.domain.vo.OrderItemInput;
import com.firstlogistics.orderservice.domain.vo.Receiver;
import com.firstlogistics.orderservice.domain.vo.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    private OrderId id;
    private Supplier supplier;
    private Receiver receiver;
    private UUID deliveryId;
    private Address deliveryAddress;
    private Money totalAmount;
    private LocalDateTime dueDate;
    private String requestMemo;
    private OrderStatus status;
    private OrderStatus previousStatus;
    private LocalDateTime orderedAt;

    @Getter(AccessLevel.NONE)
    private List<OrderItem> orderItems;

    public static Order create(
            UUID supplierCompanyId,
            UUID supplierManagerId,
            UUID receiverCompanyId,
            UUID receiverManagerId,
            String roadAddress,
            String detailAddress,
            LocalDateTime dueDate,
            String requestMemo,
            List<OrderItemInput> items,
            OrderEvents orderEvents
    ) {
        validateInput(dueDate);
        Order order = new Order(
                OrderId.of(),
                Supplier.of(supplierCompanyId, supplierManagerId),
                Receiver.of(receiverCompanyId, receiverManagerId),
                null,
                Address.of(roadAddress, detailAddress),
                Money.of(0L),
                dueDate,
                requestMemo,
                OrderStatus.PENDING,
                null,
                null,
                new ArrayList<>()
        );

        order.createOrderItems(items);
        order.calculateTotalAmount();

        orderEvents.created(order);

        return order;
    }

    /**
     * OrderJpaEntity -> Order 변환 시에만 사용
     */
    public static Order reconstitute(
            UUID id,
            UUID supplierCompanyId,
            UUID supplierManagerId,
            UUID receiverCompanyId,
            UUID receiverManagerId,
            UUID deliveryId,
            String roadAddress,
            String detailAddress,
            Long totalAmount,
            LocalDateTime dueDate,
            String requestMemo,
            OrderStatus status,
            OrderStatus previousStatus,
            LocalDateTime orderedAt,
            List<OrderItem> orderItems
    ) {
        return new Order(
                OrderId.of(id),
                Supplier.of(supplierCompanyId, supplierManagerId),
                Receiver.of(receiverCompanyId, receiverManagerId),
                deliveryId,
                Address.of(roadAddress, detailAddress),
                Money.of(totalAmount),
                dueDate,
                requestMemo,
                status,
                previousStatus,
                orderedAt,
                orderItems
        );
    }

    // 외부에서 리스트 수정 못하도록 읽기 전용으로 반환
    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    private static void validateInput(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new OrderException(OrderErrorCode.INVALID_DUE_DATE);
        }
    }

    private void createOrderItems(List<OrderItemInput> items) {
        // 주문 상세 존재 여부 체크
        if (items == null || items.isEmpty()) {
            throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_EXIST);
        }
        if (items.stream().anyMatch(Objects::isNull)) {
            throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_EXIST);
        }

        this.orderItems = new ArrayList<>();
        items.forEach(item ->
                orderItems.add(OrderItem.create(
                        item.productId(),
                        item.productName(),
                        item.unitPrice(),
                        item.quantity()
                )));
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

            // 취소 요청 상태에서 재고 예약 실패 시 취소 상태로 변경
            if(!isSuccess) {
                this.status = OrderStatus.CANCELLED;
                this.previousStatus = null;
                return;
            }

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
        // 이미 취소 요청 or 취소 된 상태인 경우
        if (this.status == OrderStatus.CANCEL_REQUESTED || this.status == OrderStatus.CANCELLED) {
            throw new OrderException(OrderErrorCode.ALREADY_CANCEL_REQUESTED);
        }
        this.status.validateNext(OrderStatus.CANCEL_REQUESTED);
        this.previousStatus = this.status;
        this.status = OrderStatus.CANCEL_REQUESTED;
    }

    public void rejectCancelRequest() {
        // 취소 요청 상태에서만 가능
        if (this.status != OrderStatus.CANCEL_REQUESTED || this.previousStatus == null) {
            throw new OrderException(OrderErrorCode.CANNOT_REJECT_CANCEL);
        }
        this.status.validateNext(this.previousStatus);
        this.status = this.previousStatus; // 이전 상태 복구
        this.previousStatus = null;
    }

}
