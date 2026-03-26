package com.firstlogistics.orderservice.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Supplier {
    private UUID companyId;
    private UUID managerId;

    public static Supplier of(UUID companyId, UUID managerId) {
        return new Supplier(companyId, managerId);
    }
}
