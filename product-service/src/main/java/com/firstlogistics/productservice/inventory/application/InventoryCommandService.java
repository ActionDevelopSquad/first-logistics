package com.firstlogistics.productservice.inventory.application;

import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryErrorCode;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryException;
import com.firstlogistics.productservice.inventory.domain.repository.InventoryRepository;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCommandService {

    private static final String LOCK_KEY_PREFIX = "lock:inventory:";
    private static final long LOCK_WAIT_TIME = 3L;
    private static final long LOCK_LEASE_TIME = 5L;

    private final InventoryRepository inventoryRepository;
    private final RedissonClient redissonClient;

    public record InventoryItem(UUID productId, int quantity) {}

    @Transactional
    public void reserve(List<InventoryItem> items) {
        for (InventoryItem item : items) {
            withLock(item.productId(), () -> {
                Inventory inventory = findInventory(item.productId());
                inventory.reserve(item.quantity());
                inventoryRepository.update(inventory);
            });
        }
    }

    @Transactional
    public void confirm(List<InventoryItem> items) {
        for (InventoryItem item : items) {
            withLock(item.productId(), () -> {
                Inventory inventory = findInventory(item.productId());
                inventory.confirm(item.quantity());
                inventoryRepository.update(inventory);
            });
        }
    }

    @Transactional
    public void cancel(List<InventoryItem> items) {
        for (InventoryItem item : items) {
            withLock(item.productId(), () -> {
                Inventory inventory = findInventory(item.productId());
                inventory.cancel(item.quantity());
                inventoryRepository.update(inventory);
            });
        }
    }

    private Inventory findInventory(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryException(InventoryErrorCode.INVENTORY_NOT_FOUND));
    }

    private void withLock(UUID productId, Runnable action) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + productId);
        try {
            if (!lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                throw new InventoryException(InventoryErrorCode.INVENTORY_LOCK_FAILED);
            }
            action.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InventoryException(InventoryErrorCode.INVENTORY_LOCK_FAILED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
