package com.firstlogistics.orderservice.domain.specification;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;

import java.time.LocalDate;
import java.util.UUID;

public record OrderSearchSpec(
        OrderStatus status,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        UUID hubId,
        LocalDate startDate,
        LocalDate endDate,
        Long minAmount,
        Long maxAmount,
        OrderSearchType searchType,
        UUID restrictedHubId,
        UUID restrictedUserId
) {}
