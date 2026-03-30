package com.firstlogistics.orderservice.infrastructure.persistence.jpa;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class OrderJpaEntity extends BaseAuditEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID supplierCompanyId;

    @Column(nullable = false)
    private UUID supplierManagerId;

    @Column(nullable = false)
    private UUID receiverCompanyId;

    @Column(nullable = false)
    private UUID receiverManagerId;

    @Column(nullable = true)
    private UUID deliveryId;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String requestMemo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = true)
    private OrderStatus previousStatus;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "order_id")
    private List<OrderItemJpaEntity> orderItems;
}
