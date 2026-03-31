package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubConnectionRepositoryImpl implements HubConnectionRepository {
    private final HubConnectionJpaRepository jpaRepository;
    private final HubConnectionMapper mapper;
}
