package com.firstlogistics.orderservice.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Receiver {
    private UUID companyId;
    private UUID managerId;

    public static Receiver of(UUID companyId, UUID managerId) {
        return new Receiver(companyId, managerId);
    }
}
