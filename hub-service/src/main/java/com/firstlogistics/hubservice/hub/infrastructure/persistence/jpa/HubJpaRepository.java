package com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubJpaRepository extends JpaRepository<HubJpaEntity, UUID> {

    boolean existsByName(String name);
}
