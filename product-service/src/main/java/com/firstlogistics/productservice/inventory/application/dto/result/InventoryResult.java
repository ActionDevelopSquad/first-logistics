package com.firstlogistics.productservice.inventory.application.dto.result;

import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import java.util.UUID;

public record InventoryResult(UUID productId, int available, int reserved) {

    public static InventoryResult from(Inventory inventory) {
        return new InventoryResult(inventory.getProductId(), inventory.getAvailable(), inventory.getReserved());
    }
}
