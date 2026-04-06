package com.firstlogistics.orderservice.application.dto.query;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
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

    public static OrderSearchSpec toSpec(OrderSearchQuery query, RoleCheck roleCheck) {
        return new OrderSearchSpec(
                query.status(),
                query.supplierCompanyId(),
                query.receiverCompanyId(),
                query.hubId(),
                query.startDate(),
                query.endDate(),
                query.minAmount(),
                query.maxAmount(),
                query.searchType(),
                roleCheck
        );
    }
}
