package com.firstlogistics.orderservice.domain.vo;

import java.util.UUID;

public record OrderItemInput(
        UUID productId,
        String productName,
        long unitPrice,
        int quantity
) {}