package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;


import java.util.List;
import java.util.UUID;

public interface HubConnectionJpaRepository extends JpaRepository<HubConnectionJpaEntity, UUID> {

    boolean existsBySourceHubIdAndDestinationHubId(UUID sourceId, UUID destinationId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    List<HubConnectionJpaEntity> findAllBySourceHubIdOrDestinationHubId(UUID sourceHubId, UUID destinationHubId);
}
