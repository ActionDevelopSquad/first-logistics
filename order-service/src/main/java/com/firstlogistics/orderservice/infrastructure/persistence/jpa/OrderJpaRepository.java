package com.firstlogistics.orderservice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"orderItems"})
    @Query("select o from OrderJpaEntity o where o.id = :id")
    Optional<OrderJpaEntity> findByIdWithItems(@Param("id") UUID id);
}
