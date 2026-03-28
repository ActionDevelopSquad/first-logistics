package com.firstlogistics.orderservice.domain.vo;

import java.util.UUID;

public record Receiver(
        UUID companyId,
        UUID managerId
) {
    public static Receiver of(UUID companyId, UUID managerId) {
        return new Receiver(companyId, managerId);
    }
}
