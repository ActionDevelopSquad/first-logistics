package com.firstlogistics.orderservice.domain.vo;

import java.util.UUID;

public record Supplier(
        UUID companyId,
        UUID managerId
) {
    public static Supplier of(UUID companyId, UUID managerId) {
        return new Supplier(companyId, managerId);
    }
}
