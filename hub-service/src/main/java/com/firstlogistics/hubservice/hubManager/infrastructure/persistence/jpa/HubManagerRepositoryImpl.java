package com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubManagerRepositoryImpl implements HubManagerRepository {
    private final HubManagerJpaRepository jpaRepository;
    private final HubManagerMapper mapper;
}
