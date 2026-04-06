package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HubConnectionRepositoryImpl implements HubConnectionRepository {
    private static final String HUB_CONNECTION_ALL_CACHE = "hubConnection:all";

    private final HubConnectionJpaRepository jpaRepository;
    private final HubConnectionMapper mapper;

    @Override
    public boolean existsBySourceAndDestination(HubId sourceHubId, HubId destinationHubId) {
        return jpaRepository.existsBySourceHubIdAndDestinationHubId(sourceHubId.id(), destinationHubId.id());
    }

    @Override
    @CacheEvict(cacheNames = HUB_CONNECTION_ALL_CACHE, allEntries = true)
    public HubConnection save(HubConnection hubConnection) {
        try{
            HubConnectionJpaEntity savedEntity =  jpaRepository.save(mapper.toJpaEntity(hubConnection));
            return mapper.toDomain(savedEntity);
        }
        catch (DataIntegrityViolationException e) {
            if(hasConstraintName(e, HubConnectionConstraints.UK_HUB_CONNECTION_HUB_ID))
                throw new HubConnectionException(HubConnectionErrorCode.DUPLICATE_HUB_CONNECTION);
            throw e;
        }
    }

    @Override
    @Cacheable(cacheNames = HUB_CONNECTION_ALL_CACHE)
    public List<HubConnection> findAll() {
        List<HubConnectionJpaEntity> entities = jpaRepository.findAll();
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public HubConnection findById(HubConnectionId id) {
        HubConnectionJpaEntity hubConnection = jpaRepository.findById(id.id())
                .orElseThrow(()-> new HubConnectionException(HubConnectionErrorCode.HUB_CONNECTION_NOT_FOUND));

        return mapper.toDomain(hubConnection);
    }

    @Override
    @CacheEvict(cacheNames = HUB_CONNECTION_ALL_CACHE, allEntries = true)
    public void delete(HubConnection hubConnection) {
        jpaRepository.delete(mapper.toJpaEntity(hubConnection));
    }

    private boolean hasConstraintName(Throwable throwable, String expectedConstraintName ){
        Throwable cause = throwable;
        while(cause!=null){
            if(cause instanceof  org.hibernate.exception.ConstraintViolationException cve){
                String constraintName = cve.getConstraintName();
                if(expectedConstraintName.equalsIgnoreCase(constraintName)){
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }
}
