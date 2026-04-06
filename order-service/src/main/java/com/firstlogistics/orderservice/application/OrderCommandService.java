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
import com.firstlogistics.orderservice.application.port.OrderAuthorityCheckPort;
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
    private final OrderAuthorityCheckPort authorityCheck;
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
        Order order = getOrderWithAuthorityCheck(orderId, AuthorityAction.ACCEPT_OR_CANCEL);

        order.accept();

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
        Order order = getOrderWithAuthorityCheck(orderId, AuthorityAction.REQUEST_CANCEL)
;
        order.requestCancel();

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
        Order order = getOrderWithAuthorityCheck(orderId, AuthorityAction.ACCEPT_OR_CANCEL);

        order.rejectCancelRequest();

        orderRepository.save(order);

        return order.getStatus().name();
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        Order order = getOrderWithAuthorityCheck(orderId, AuthorityAction.DELETE);

        if (!order.isDeletable()) {
            throw new OrderException(OrderErrorCode.NOT_IN_DELETABLE_STATUS);
        }

        orderRepository.deleteById(order.getId(), userContext.getCurrentUserId());
    }

    @Transactional
    public void assignDelivery(UUID orderId, UUID deliveryId) {
        Order order = getOrder(orderId);

        order.assignDelivery(deliveryId);

        orderRepository.save(order);
    }

    @Transactional
    public void cancelByDeliveryFailure(UUID orderId) {
        Order order = getOrder(orderId);

        order.rejectBySystem();

        Events.trigger(OrderCancelledEvent.from(order));

        orderRepository.save(order);
    }

    private String processCancellation(UUID orderId, OrderCancelType cancelType) {
        Order order  = getOrderWithAuthorityCheck(orderId, AuthorityAction.ACCEPT_OR_CANCEL);

        order.cancel(cancelType);

        orderRepository.save(order);

        Events.trigger(OrderCancelledEvent.from(order));

        return order.getStatus().name();
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    // 권한 체크용 공통 메서드
    private Order getOrderWithAuthorityCheck(UUID orderId, AuthorityAction action) {
        Order order = orderRepository.findById(OrderId.of(orderId))
                    .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
        UUID myHubId = userContext.isHubManager()
                ? hubPort.getHubManagerByUserId(userContext.getCurrentUserId())
                .map(HubManagerResponse::hubId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.HUB_MANAGER_NOT_FOUND))
                : null;

        boolean hasAuthority = switch (action) {
            case ACCEPT_OR_CANCEL -> authorityCheck.canAcceptOrCancel(order.getSupplier().hubId(), order.getSupplier().managerId(), myHubId);
            case DELETE -> authorityCheck.canDelete(order.getSupplier().hubId(), myHubId);
            case REQUEST_CANCEL -> authorityCheck.canRequestCancel(order.getReceiver().managerId());
        };

        if (!hasAuthority) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        return order;
    }

    private enum AuthorityAction { ACCEPT_OR_CANCEL, DELETE, REQUEST_CANCEL }
}
