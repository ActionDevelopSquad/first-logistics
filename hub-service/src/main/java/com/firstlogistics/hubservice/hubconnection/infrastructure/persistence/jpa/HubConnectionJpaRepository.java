package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface HubConnectionJpaRepository extends JpaRepository<HubConnectionJpaEntity, UUID> {

    boolean existsBySourceHubIdAndDestinationHubId(UUID sourceId, UUID destinationId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update HubConnectionJpaEntity hc
           set hc.status = :status
         where (hc.sourceHubId = :hubId or hc.destinationHubId = :hubId)
           and hc.status <> :status
    """)
    int updateStatusByHubId(@Param("hubId") UUID hubId,
                            @Param("status") HubConnectionStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from HubConnectionJpaEntity hc
         where hc.sourceHubId = :hubId
            or hc.destinationHubId = :hubId
    """)
    int deleteByHubId(@Param("hubId") UUID hubId);
}
