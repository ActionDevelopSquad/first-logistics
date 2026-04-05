package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.command.CreateOrderCommand;
import com.firstlogistics.orderservice.application.port.CompanyPort;
import com.firstlogistics.orderservice.application.port.HubPort;
import com.firstlogistics.orderservice.application.port.UserContextPort;
import com.firstlogistics.orderservice.application.port.dto.HubManagerResponse;
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
    private final HubPort hubPort;
    private final UserContextPort userContext;

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
        order.accept(getHubIdByUserId(userContext.getCurrentUserId()), roleCheck);

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
        order.rejectCancelRequest(getHubIdByUserId(userContext.getCurrentUserId()), roleCheck);

        orderRepository.save(order);

        return order.getStatus().name();
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        Order order = getOrder(orderId);
        UUID userId = userContext.getCurrentUserId();

        if (!roleCheck.canDelete(order.getId(),
                order.getSupplier().hubId(),
                getHubIdByUserId(userId))
        ) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }

        orderRepository.deleteById(order.getId(), userId);
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private String processCancellation(UUID orderId, OrderCancelType cancelType) {
        Order order  = getOrder(orderId);
        order.cancel(cancelType, getHubIdByUserId(userContext.getCurrentUserId()), roleCheck);

        orderRepository.save(order);

        Events.trigger(OrderCancelledEvent.from(order));

        return order.getStatus().name();
    }

    private UUID getHubIdByUserId(UUID userId) {
        if (!userContext.isHubManager()) return null;
        return hubPort.getHubManagerByUserId(userId).map(HubManagerResponse::hubId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.HUB_MANAGER_NOT_FOUND));
    }
}
