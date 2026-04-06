package com.firstlogistics.productservice.inventory.infrastructure.persistence.jpa;

import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_inventory")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class InventoryJpaEntity extends BaseAuditEntity {

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int available;

    @Column(nullable = false)
    private int reserved;
}
