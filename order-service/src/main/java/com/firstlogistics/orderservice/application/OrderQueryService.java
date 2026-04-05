package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.query.OrderSearchQuery;
import com.firstlogistics.orderservice.application.dto.result.OrderDetailResult;
import com.firstlogistics.orderservice.application.dto.result.OrderSummaryResult;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.repository.OrderQueryRepository;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;
    private final OrderRepository orderRepository;
    private final RoleCheck roleCheck;

    public Page<OrderSummaryResult> getOrders(OrderSearchQuery query, Pageable pageable) {
        return orderQueryRepository.findAll(OrderSearchQuery.toSpec(query, roleCheck), pageable)
                .map(OrderSummaryResult::from);
    }

    public OrderDetailResult getOrder(UUID orderId) {
        Order order =  orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        if (!roleCheck.canView(order)) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }

        return OrderDetailResult.from(order);
    }
}
