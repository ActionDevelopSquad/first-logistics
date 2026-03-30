package com.firstlogistics.productservice.inventory.domain.entity;

import com.firstlogistics.productservice.inventory.domain.exception.InventoryErrorCode;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryException;
import java.util.UUID;

public class Inventory {
    private final UUID productId;
    private int available;
    private int reserved;

    private Inventory(UUID productId, int available, int reserved) {
        validate(productId, available, reserved);

        this.productId = productId;
        this.available = available;
        this.reserved = reserved;
    }

    public static Inventory create(UUID productId, int available, int reserved) {
        return new Inventory(productId, available, reserved);
    }

    public void reserve(int quantity) {
        validatePositive(quantity);

        if (available < quantity) {
            throw new InventoryException(InventoryErrorCode.INSUFFICIENT_AVAILABLE_QUANTITY);
        }

        available -= quantity;
        reserved += quantity;

        validateInvariant();
    }

    public void confirm(int quantity) {
        validatePositive(quantity);

        if (reserved < quantity) {
            throw new IllegalStateException("예약 재고 부족");
        }

        reserved -= quantity;

        validateInvariant();
    }

    public void cancel(int quantity) {
        validatePositive(quantity);

        if (reserved < quantity) {
            throw new InventoryException(InventoryErrorCode.INSUFFICIENT_AVAILABLE_QUANTITY);
        }

        reserved -= quantity;
        available += quantity;

        validateInvariant();
    }

    public void increase(int quantity) {
        validatePositive(quantity);

        available += quantity;

        validateInvariant();
    }

    public void decrease(int quantity) {
        validatePositive(quantity);

        if (available < quantity) {
            throw new InventoryException(InventoryErrorCode.INSUFFICIENT_AVAILABLE_QUANTITY);
        }

        available -= quantity;

        validateInvariant();
    }

    private void validate(UUID productId, int available, int reserved) {
        if (productId == null) {
            throw new InventoryException(InventoryErrorCode.INVALID_PRODUCT_ID);
        }
        if (available < 0 || reserved < 0) {
            throw new InventoryException(InventoryErrorCode.INVALID_QUANTITY);
        }
    }

    private void validatePositive(int quantity) {
        if (quantity <= 0) {
            throw new InventoryException(InventoryErrorCode.INVALID_QUANTITY);
        }
    }

    private void validateInvariant() {
        if (available < 0 || reserved < 0) {
            throw new InventoryException(InventoryErrorCode.INVALID_QUANTITY);
        }
    }
}
