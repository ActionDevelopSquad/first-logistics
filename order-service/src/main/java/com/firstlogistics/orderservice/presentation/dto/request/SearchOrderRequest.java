package com.firstlogistics.orderservice.presentation.dto.request;

import com.firstlogistics.orderservice.application.dto.query.OrderSearchQuery;

import java.time.LocalDate;
import java.util.UUID;

public record SearchOrderRequest(
        String status,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        UUID hubId,
        LocalDate startDate,
        LocalDate endDate,
        Long minAmount,
        Long maxAmount,
        String searchType
) {
    public OrderSearchQuery toQuery() {
        return OrderSearchQuery.of(
                this.status,
                this.supplierCompanyId,
                this.receiverCompanyId,
                this.hubId,
                this.startDate,
                this.endDate,
                this.minAmount,
                this.maxAmount,
                this.searchType
        );
    }
}
