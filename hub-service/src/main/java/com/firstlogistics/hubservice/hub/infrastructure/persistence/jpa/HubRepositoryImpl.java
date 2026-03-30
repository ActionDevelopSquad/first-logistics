package com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {
    private final HubJpaRepository jpaRepository;
    private final HubMapper mapper;

    @Override
    public boolean existsByHubName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public Hub save(Hub hub) {
        jpaRepository.save(mapper.toJpaEntity(hub));
        return hub;
    }
}

