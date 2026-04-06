package com.firstlogistics.productservice.product.domain.repository;

import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.specification.ProductSearchSpec;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    Page<Product> findAll(ProductSearchSpec spec, Pageable pageable);
}
