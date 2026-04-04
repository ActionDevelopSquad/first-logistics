package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryJpaEntity, UUID> {

	boolean existsByOrderId(UUID orderId);

	Optional<DeliveryJpaEntity> findByOrderId(UUID orderId);
}
