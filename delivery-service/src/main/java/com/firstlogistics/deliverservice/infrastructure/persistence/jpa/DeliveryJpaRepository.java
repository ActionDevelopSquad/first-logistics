package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryJpaEntity, UUID> {

	boolean existsByOrderId(UUID orderId);

	Optional<DeliveryJpaEntity> findByOrderId(UUID orderId);

	@Query("SELECT d FROM DeliveryJpaEntity d LEFT JOIN FETCH d.routes WHERE d.id = :id")
	Optional<DeliveryJpaEntity> findByIdWithRoutes(@Param("id") UUID id);
}
