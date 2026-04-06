package com.firstlogistics.orderservice.domain.specification;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.service.RoleCheck;

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
        RoleCheck roleCheck
) {
    public boolean isMaster() {
        return roleCheck.isMaster();
    }

    public boolean isHubManager() {
        return roleCheck.isHubManager();
    }

    public boolean isCompanyManager() {
        return roleCheck.isCompanyManager();
    }

    public UUID getMyHubId() {
        return roleCheck.getCurrentUserHubId();
    }

    public UUID getMyUserId() {
        return roleCheck.getCurrentUserId();
    }

}
