package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.product.application.dto.query.ProductSearchQuery;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductRepository productRepository;

    public Page<ProductResult> search(ProductSearchQuery query, Pageable pageable) {
        return productRepository.findAll(query.toSpec(), pageable)
                .map(ProductResult::from);
    }

    public ProductResult getById(UUID productId) {
        return productRepository.findById(productId)
                .map(ProductResult::from)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }
}
