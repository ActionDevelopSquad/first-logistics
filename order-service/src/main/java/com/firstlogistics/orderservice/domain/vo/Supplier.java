package com.firstlogistics.orderservice.domain.vo;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;

import java.util.UUID;

public record Supplier(
        UUID companyId,
        UUID managerId
) {
    public Supplier {
        if (companyId == null) {
            throw new OrderException(OrderErrorCode.SUPPLIER_COMPANY_REQUIRED);
        }
        if (managerId == null) {
            throw new OrderException(OrderErrorCode.SUPPLIER_MANAGER_REQUIRED);
        }
    }

    public static Supplier of(UUID companyId, UUID managerId) {
        return new Supplier(companyId, managerId);
    }
}
