package com.firstlogistics.productservice.inventory.domain.repository;

import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {

    Inventory save(Inventory inventory);

    Optional<Inventory> findByProductId(UUID productId);
}
