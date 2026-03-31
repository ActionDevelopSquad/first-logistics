package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubConnectionJpaRepository extends JpaRepository<HubConnectionJpaEntity, UUID> {
}
