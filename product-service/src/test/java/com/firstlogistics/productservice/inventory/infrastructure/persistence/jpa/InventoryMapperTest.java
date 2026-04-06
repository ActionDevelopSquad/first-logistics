package com.firstlogistics.productservice.inventory.infrastructure.persistence.jpa;

import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMapperTest {

    private static final UUID PRODUCT_ID = UUID.randomUUID();

    @Test
    @DisplayName("도메인 엔티티를 JPA 엔티티로 변환한다")
    void toJpaEntity() {
        // given
        Inventory inventory = Inventory.create(PRODUCT_ID, 100, 20);

        // when
        InventoryJpaEntity jpaEntity = InventoryMapper.toJpaEntity(inventory);

        // then
        assertThat(jpaEntity.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(jpaEntity.getAvailable()).isEqualTo(100);
        assertThat(jpaEntity.getReserved()).isEqualTo(20);
    }

    @Test
    @DisplayName("JPA 엔티티를 도메인 엔티티로 변환한다")
    void toDomain() {
        // given
        InventoryJpaEntity jpaEntity = new InventoryJpaEntity(PRODUCT_ID, 100, 20);

        // when
        Inventory inventory = InventoryMapper.toDomain(jpaEntity);

        // then
        assertThat(inventory.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(inventory.getAvailable()).isEqualTo(100);
        assertThat(inventory.getReserved()).isEqualTo(20);
    }
}
