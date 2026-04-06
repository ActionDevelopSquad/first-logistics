package com.firstlogistics.productservice.inventory.infrastructure.persistence.jpa;

import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryMapper {

    public static InventoryJpaEntity toJpaEntity(Inventory inventory) {
        return new InventoryJpaEntity(
                inventory.getProductId(),
                inventory.getAvailable(),
                inventory.getReserved()
        );
    }

    public static Inventory toDomain(InventoryJpaEntity entity) {
        return Inventory.reconstitute(
                entity.getProductId(),
                entity.getAvailable(),
                entity.getReserved()
        );
    }
}
