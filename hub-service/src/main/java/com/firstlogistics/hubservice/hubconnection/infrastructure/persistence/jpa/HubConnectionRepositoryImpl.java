package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubConnectionRepositoryImpl implements HubConnectionRepository {
    private final HubConnectionJpaRepository jpaRepository;
    private final HubConnectionMapper mapper;

    @Override
    public boolean existsBySourceAndDestination(HubId sourceHubId, HubId destinationHubId) {
        return jpaRepository.existsBySourceHubIdAndDestinationHubId(sourceHubId.id(), destinationHubId.id());
    }

    @Override
    public HubConnection save(HubConnection hubConnection) {
        HubConnectionJpaEntity savedEntity =  jpaRepository.save(mapper.toJpaEntity(hubConnection));
        return mapper.toDomain(savedEntity);
    }
}
