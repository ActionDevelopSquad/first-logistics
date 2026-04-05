package com.firstlogistics.productservice.product.infrastructure.persistence.jpa;

import static com.firstlogistics.productservice.product.infrastructure.persistence.jpa.QProductJpaEntity.productJpaEntity;

import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import com.firstlogistics.productservice.product.domain.specification.ProductSearchSpec;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = ProductMapper.toJpaEntity(product);
        ProductJpaEntity saved = productJpaRepository.save(entity);
        return ProductMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return productJpaRepository.findById(productId)
                .map(ProductMapper::toDomain);
    }

    @Override
    public Page<Product> findAll(ProductSearchSpec spec, Pageable pageable) {
        BooleanBuilder builder = ProductSpecification.from(spec);

        List<ProductJpaEntity> entities = queryFactory
                .selectFrom(productJpaEntity)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(resolveOrderSpecifiers(pageable.getSort()))
                .fetch();

        List<Product> content = entities.stream()
                .map(ProductMapper::toDomain)
                .toList();

        Long totalCount = queryFactory
                .select(productJpaEntity.count())
                .from(productJpaEntity)
                .where(builder)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void delete(UUID productId, UUID deletedBy) {
        productJpaRepository.findById(productId)
                .ifPresent(entity -> {
                    entity.softDelete(deletedBy);
                    productJpaRepository.save(entity);
                });
    }

    private OrderSpecifier<?>[] resolveOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> specifiers = sort.stream()
                .map(order -> {
                    boolean isAsc = order.isAscending();
                    return switch (order.getProperty()) {
                        case "createdAt" ->
                                isAsc ? productJpaEntity.createdAt.asc() : productJpaEntity.createdAt.desc();
                        case "updatedAt" ->
                                isAsc ? productJpaEntity.updatedAt.asc() : productJpaEntity.updatedAt.desc();
                        default -> throw new ProductException(ProductErrorCode.INVALID_SORT_FIELD);
                    };
                })
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        if (specifiers.isEmpty()) {
            specifiers.add(productJpaEntity.createdAt.desc());
        }
        specifiers.add(productJpaEntity.id.asc());

        return specifiers.toArray(new OrderSpecifier[0]);
    }
}
