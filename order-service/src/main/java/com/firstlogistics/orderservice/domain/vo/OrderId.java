package com.firstlogistics.orderservice.domain.vo;

import lombok.*;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderId {
    private UUID id;

    public static OrderId of() {
        return OrderId.of(UUID.randomUUID());
    }

    public static OrderId of(UUID id) {
        return new OrderId(id);
    }
}
