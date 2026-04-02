package com.firstlogistics.aiservice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AILogJpaRepository extends JpaRepository<AILogJpaEntity, UUID> {
}
