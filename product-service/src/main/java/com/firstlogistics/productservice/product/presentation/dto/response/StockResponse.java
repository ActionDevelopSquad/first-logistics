package com.firstlogistics.productservice.product.presentation.dto.response;

import com.firstlogistics.productservice.inventory.application.dto.result.InventoryResult;
import java.util.UUID;

public record StockResponse(
        UUID productId,
        int available,
        int reserved
) {
    public static StockResponse from(InventoryResult result) {
        return new StockResponse(result.productId(), result.available(), result.reserved());
    }
}
