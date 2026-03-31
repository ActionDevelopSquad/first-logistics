package com.firstlogistics.orderservice.domain.vo;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;

import java.util.UUID;

public record Receiver(
        UUID companyId,
        UUID managerId
) {
    public Receiver {
        if (companyId == null) {
            throw new OrderException(OrderErrorCode.RECEIVER_COMPANY_REQUIRED);
        }
        if (managerId == null) {
            throw new OrderException(OrderErrorCode.RECEIVER_MANAGER_REQUIRED);
        }
    }

    public static Receiver of(UUID companyId, UUID managerId) {
        return new Receiver(companyId, managerId);
    }
}
