package com.firstlogistics.productservice.product.application.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductCommand(
        UUID requesterId,
        String requesterRole,
        UUID companyId,
        String name,
        BigDecimal price,
        int stock
) {}
