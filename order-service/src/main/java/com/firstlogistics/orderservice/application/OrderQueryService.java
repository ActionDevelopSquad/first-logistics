package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.query.OrderSearchQuery;
import com.firstlogistics.orderservice.application.dto.result.OrderDetailResult;
import com.firstlogistics.orderservice.application.dto.result.OrderSummaryResult;
import com.firstlogistics.orderservice.application.port.HubPort;
import com.firstlogistics.orderservice.application.port.UserContextPort;
import com.firstlogistics.orderservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.repository.OrderQueryRepository;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.application.port.OrderAuthorityCheckPort;
import com.firstlogistics.orderservice.domain.specification.OrderSearchSpec;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;
    private final OrderRepository orderRepository;
    private final OrderAuthorityCheckPort authorityCheck;
    private final UserContextPort userContext;
    private final HubPort hubPort;

    public Page<OrderSummaryResult> getOrders(OrderSearchQuery query, Pageable pageable) {
        UUID restrictedHubId = null;
        UUID restrictedUserId = null;

        if (userContext.isHubManager()) {
            restrictedHubId = getHubIdByUserId(userContext.getCurrentUserId());
        } else if (userContext.isCompanyManager()) {
            restrictedUserId = userContext.getCurrentUserId();
        }

        OrderSearchSpec spec = query.toSpec(restrictedHubId, restrictedUserId);

        return orderQueryRepository.findAll(spec, pageable)
                .map(OrderSummaryResult::from);
    }

    public OrderDetailResult getOrder(UUID orderId) {
        Order order = orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        UUID myHubId = getHubIdByUserId(userContext.getCurrentUserId());

        if (!authorityCheck.canView(
                order.getSupplier().hubId(),
                order.getSupplier().managerId(),
                order.getReceiver().managerId(),
                myHubId
        )) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }

        return OrderDetailResult.from(order);
    }

    private UUID getHubIdByUserId(UUID userId) {
        if (!userContext.isHubManager()) return null;
        return hubPort.getHubManagerByUserId(userId)
                .map(HubManagerResponse::hubId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.HUB_MANAGER_NOT_FOUND));
    }
}
