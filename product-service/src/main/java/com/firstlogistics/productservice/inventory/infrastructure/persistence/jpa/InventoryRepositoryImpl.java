package com.firstlogistics.productservice.inventory.infrastructure.persistence.jpa;

import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import com.firstlogistics.productservice.inventory.domain.repository.InventoryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;

    @Override
    public Inventory save(Inventory inventory) {
        InventoryJpaEntity entity = InventoryMapper.toJpaEntity(inventory);
        InventoryJpaEntity saved = inventoryJpaRepository.save(entity);
        return InventoryMapper.toDomain(saved);
    }

    @Override
    public Inventory update(Inventory inventory) {
        return save(inventory);
    }

    @Override
    public Optional<Inventory> findByProductId(UUID productId) {
        return inventoryJpaRepository.findByProductId(productId)
                .map(InventoryMapper::toDomain);
    }
}
