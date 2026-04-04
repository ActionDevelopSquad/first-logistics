package com.firstlogistics.orderservice.domain.repository;

import com.firstlogistics.orderservice.domain.repository.dto.OrderSummaryDto;
import com.firstlogistics.orderservice.domain.specification.OrderSearchSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderQueryRepository {

    Page<OrderSummaryDto> findAll(OrderSearchSpec spec, Pageable pageable);
}
