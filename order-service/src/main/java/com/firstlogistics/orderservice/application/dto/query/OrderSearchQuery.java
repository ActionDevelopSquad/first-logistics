package com.firstlogistics.orderservice.application.dto.query;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.specification.OrderSearchSpec;
import com.firstlogistics.orderservice.domain.specification.OrderSearchType;

import java.time.LocalDate;
import java.util.UUID;

public record OrderSearchQuery(
        OrderStatus status,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        UUID hubId,
        LocalDate startDate,
        LocalDate endDate,
        Long minAmount,
        Long maxAmount,
        OrderSearchType searchType
) {
    public static OrderSearchQuery of(
            String statusName,
            UUID supplierCompanyId,
            UUID receiverCompanyId,
            UUID hubId,
            LocalDate startDate,
            LocalDate endDate,
            Long minAmount,
            Long maxAmount,
            String searchType
    ) {
        return new OrderSearchQuery(
                OrderStatus.from(statusName),
                supplierCompanyId,
                receiverCompanyId,
                hubId,
                startDate,
                endDate,
                minAmount,
                maxAmount,
                OrderSearchType.from(searchType)
        );
    }

    public OrderSearchSpec toSpec(UUID restrictedHubId, UUID restrictedUserId) {
        return new OrderSearchSpec(
                this.status,
                this.supplierCompanyId,
                this.receiverCompanyId,
                this.hubId,
                this.startDate,
                this.endDate,
                this.minAmount,
                this.maxAmount,
                this.searchType,
                restrictedHubId,
                restrictedUserId
        );
    }
}
