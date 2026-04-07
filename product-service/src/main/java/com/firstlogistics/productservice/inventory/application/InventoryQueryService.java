package com.firstlogistics.productservice.inventory.application;

import com.firstlogistics.productservice.inventory.application.dto.result.InventoryResult;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryErrorCode;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryException;
import com.firstlogistics.productservice.inventory.domain.repository.InventoryRepository;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryQueryService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryResult getByProductId(UUID productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        return inventoryRepository.findByProductId(productId)
                .map(InventoryResult::from)
                .orElseThrow(() -> new InventoryException(InventoryErrorCode.INVENTORY_NOT_FOUND));
    }
}
