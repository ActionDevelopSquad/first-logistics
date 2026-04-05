package com.firstlogistics.productservice.product.application.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductCommand(
        UUID requesterId,
        String requesterRole,
        UUID productId,
        String name,
        BigDecimal price
) {}
