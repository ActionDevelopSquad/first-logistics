package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.CreateOrderCommand;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.enums.OrderCancelType;
import com.firstlogistics.orderservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.orderservice.domain.event.OrderCancelledEvent;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.domain.vo.OrderItemInput;
import common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;

    @Transactional
    public UUID createOrder(CreateOrderCommand command) {
        // TODO: 나중에 userId와 검증 로직 필요

        UUID hubId = UUID.randomUUID();

        List<OrderItemInput> itemInputs = command.items().stream()
                .map(CreateOrderCommand.OrderItemCommand::toDomainInput)
                .toList();

        Order order = Order.create(
                command.supplierCompanyId(),
                command.supplierManagerId(),
                hubId,
                command.receiverCompanyId(),
                command.receiverManagerId(),
                command.roadAddress(),
                command.detailAddress(),
                command.dueDate(),
                command.requestMemo(),
                itemInputs
        );

        orderRepository.save(order);

        Events.trigger(OrderCreatedEvent.from(order));

        return order.getId().id();
    }

    @Transactional
    public String acceptOrder(UUID userId, UUID orderId) {
        Order order = getOrder(orderId);
        order.accept();

        orderRepository.save(order);

        Events.trigger(OrderAcceptedEvent.from(order));

        return order.getStatus().name();
    }

    // 주문 거절로 인한 취소
    @Transactional
    public String rejectOrder(UUID userId, UUID orderId) {
        return processCancellation(userId, orderId, OrderCancelType.SUPPLIER_CANCEL);
    }

    // 관리자가 직접 주문 취소
    @Transactional
    public String cancelOrder(UUID userId, UUID orderId) {
        return processCancellation(userId, orderId, OrderCancelType.ADMIN_CANCEL);
    }

    @Transactional
    public String requestCancel(UUID userId, UUID orderId) {
        Order order = getOrder(orderId);
        order.requestCancel();

        orderRepository.save(order);

        return order.getStatus().name();
    }

    // 주문 취소 요청 승인으로 인한 취소
    @Transactional
    public String approveCancelRequest(UUID userId, UUID orderId) {
        return processCancellation(userId, orderId, OrderCancelType.ORDERER_REQUEST);
    }

    @Transactional
    public String rejectCancelRequest(UUID userId, UUID orderId) {
        Order order = getOrder(orderId);
        order.rejectCancelRequest();

        orderRepository.save(order);

        return order.getStatus().name();
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private String processCancellation(UUID userId, UUID orderId, OrderCancelType cancelType) {
        Order order  = getOrder(orderId);
        order.cancel(cancelType);

        orderRepository.save(order);

        Events.trigger(OrderCancelledEvent.from(order));

        return order.getStatus().name();
    }
}
