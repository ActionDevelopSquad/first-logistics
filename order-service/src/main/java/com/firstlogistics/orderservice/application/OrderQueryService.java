package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.query.OrderSearchQuery;
import com.firstlogistics.orderservice.application.dto.result.OrderSummaryResult;
import com.firstlogistics.orderservice.domain.repository.OrderQueryRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;
    private final RoleCheck roleCheck;

    public Page<OrderSummaryResult> getOrders(OrderSearchQuery query, Pageable pageable) {
        return orderQueryRepository.findAll(OrderSearchQuery.toSpec(query, roleCheck), pageable)
                .map(OrderSummaryResult::from);
    }
}
