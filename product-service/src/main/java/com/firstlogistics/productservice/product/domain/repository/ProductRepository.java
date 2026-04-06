package com.firstlogistics.productservice.product.domain.repository;

import com.firstlogistics.productservice.product.domain.entity.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);
}
