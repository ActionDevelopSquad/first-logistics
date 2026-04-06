package com.firstlogistics.orderservice.presentation.dto.request;

import com.firstlogistics.orderservice.application.dto.query.OrderSearchQuery;
import jakarta.validation.constraints.AssertTrue;

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
    @AssertTrue(message = "시작일은 종료일보다 이전이어야 합니다.")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) return true;
        return !startDate.isAfter(endDate);
    }

    @AssertTrue(message = "최소 금액은 최대 금액보다 클 수 없습니다.")
    public boolean isValidAmountRange() {
        if (minAmount == null || maxAmount == null) return true;
        return minAmount <= maxAmount;
    }

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
