package com.firstlogistics.productservice.product.infrastructure.persistence.jpa;

import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private static final UUID ID = UUID.randomUUID();
    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final UUID HUB_ID = UUID.randomUUID();

    @Test
    @DisplayName("도메인 엔티티를 JPA 엔티티로 변환한다")
    void toJpaEntity() {
        // given
        Product product = Product.reconstitute(
                ID, COMPANY_ID, HUB_ID, "마른오징어",
                Money.krw(15000), ProductStatus.SELLING
        );

        // when
        ProductJpaEntity jpaEntity = ProductMapper.toJpaEntity(product);

        // then
        assertThat(jpaEntity.getId()).isEqualTo(ID);
        assertThat(jpaEntity.getCompanyId()).isEqualTo(COMPANY_ID);
        assertThat(jpaEntity.getHubId()).isEqualTo(HUB_ID);
        assertThat(jpaEntity.getName()).isEqualTo("마른오징어");
        assertThat(jpaEntity.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(15000));
        assertThat(jpaEntity.getStatus()).isEqualTo(ProductStatus.SELLING);
    }

    @Test
    @DisplayName("JPA 엔티티를 도메인 엔티티로 변환한다")
    void toDomain() {
        // given
        ProductJpaEntity jpaEntity = new ProductJpaEntity(
                ID, COMPANY_ID, HUB_ID, "마른오징어",
                BigDecimal.valueOf(15000), ProductStatus.SELLING
        );

        // when
        Product product = ProductMapper.toDomain(jpaEntity);

        // then
        assertThat(product.getId()).isEqualTo(ID);
        assertThat(product.getCompanyId()).isEqualTo(COMPANY_ID);
        assertThat(product.getHubId()).isEqualTo(HUB_ID);
        assertThat(product.getName()).isEqualTo("마른오징어");
        assertThat(product.getPrice().amount()).isEqualByComparingTo(BigDecimal.valueOf(15000));
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SELLING);
        assertThat(product.isSellable()).isTrue();
    }

    @Test
    @DisplayName("STOPPED 상태의 JPA 엔티티를 도메인으로 변환한다")
    void toDomain_stopped() {
        // given
        ProductJpaEntity jpaEntity = new ProductJpaEntity(
                ID, COMPANY_ID, HUB_ID, "마른오징어",
                BigDecimal.valueOf(15000), ProductStatus.STOPPED
        );

        // when
        Product product = ProductMapper.toDomain(jpaEntity);

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.STOPPED);
        assertThat(product.isSellable()).isFalse();
    }
}
