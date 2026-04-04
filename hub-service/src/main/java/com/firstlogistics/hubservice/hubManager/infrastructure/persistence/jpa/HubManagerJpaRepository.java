package com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubManagerJpaRepository extends JpaRepository<HubManagerJpaEntity, UUID> {
}
