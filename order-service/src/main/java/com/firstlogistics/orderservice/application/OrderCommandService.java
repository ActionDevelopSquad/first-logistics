package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.command.CreateOrderCommand;
import com.firstlogistics.orderservice.application.port.CompanyPort;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.enums.OrderCancelType;
import com.firstlogistics.orderservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.orderservice.domain.event.OrderCancelledEvent;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.domain.vo.OrderItemInput;
import com.firstlogistics.orderservice.application.port.dto.CompanyResponse;
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
    private final CompanyPort companyPort;
    private final RoleCheck roleCheck;

    @Transactional
    public UUID createOrder(CreateOrderCommand command) {

        CompanyResponse supplierCompany = companyPort.getCompanyById(command.supplierCompanyId());

        List<OrderItemInput> itemInputs = command.items().stream()
                .map(CreateOrderCommand.OrderItemCommand::toDomainInput)
                .toList();

        Order order = Order.create(
                command.supplierCompanyId(),
                command.supplierManagerId(),
                supplierCompany.hubId(),
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
    public String acceptOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.accept(roleCheck);

        orderRepository.save(order);

        Events.trigger(OrderAcceptedEvent.from(order));

        return order.getStatus().name();
    }

    // 주문 거절로 인한 취소
    @Transactional
    public String rejectOrder(UUID orderId) {
        return processCancellation(orderId, OrderCancelType.SUPPLIER_CANCEL);
    }

    // 관리자가 직접 주문 취소
    @Transactional
    public String cancelOrder(UUID orderId) {
        return processCancellation(orderId, OrderCancelType.ADMIN_CANCEL);
    }

    @Transactional
    public String requestCancel(UUID orderId) {
        Order order = getOrder(orderId);
        order.requestCancel(roleCheck);

        orderRepository.save(order);

        return order.getStatus().name();
    }

    // 주문 취소 요청 승인으로 인한 취소
    @Transactional
    public String approveCancelRequest(UUID orderId) {
        return processCancellation(orderId, OrderCancelType.ORDERER_REQUEST);
    }

    @Transactional
    public String rejectCancelRequest(UUID orderId) {
        Order order = getOrder(orderId);
        order.rejectCancelRequest(roleCheck);

        orderRepository.save(order);

        return order.getStatus().name();
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private String processCancellation(UUID orderId, OrderCancelType cancelType) {
        Order order  = getOrder(orderId);
        order.cancel(cancelType, roleCheck);

        orderRepository.save(order);

        Events.trigger(OrderCancelledEvent.from(order));

        return order.getStatus().name();
    }
}
