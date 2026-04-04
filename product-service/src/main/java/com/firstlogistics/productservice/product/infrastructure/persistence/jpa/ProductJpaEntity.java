package com.firstlogistics.productservice.product.infrastructure.persistence.jpa;

import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_product")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class ProductJpaEntity extends BaseAuditEntity {

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID companyId;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID hubId;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProductStatus status;
}
