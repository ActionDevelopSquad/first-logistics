package com.firstlogistics.productservice.product.application.dto.command;

import java.util.UUID;

public record ChangeProductStatusCommand(
        UUID requesterId,
        String requesterRole,
        UUID productId,
        String status
) {}
