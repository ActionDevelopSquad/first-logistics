package com.firstlogistics.orderservice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"orderItems"})
    Optional<OrderJpaEntity> findById(UUID id);

    boolean existsByIdAndSupplierManagerId(UUID id, UUID supplierManagerId);

    boolean existsByIdAndReceiverManagerId(UUID id, UUID receiverManagerId);

    boolean existsByIdAndSupplierHubId(UUID id, UUID hubId);
}
