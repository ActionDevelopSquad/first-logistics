package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import com.firstlogistics.hubservice.hubconnection.infrastructure.cache.HubConnectionCacheDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubConnectionRepositoryImpl implements HubConnectionRepository {
    private static final String HUB_CONNECTION_ALL_CACHE = "hubConnection:all";
    private static final String HUB_CONNECTION_ALL_KEY = "all";

    private final CacheManager cacheManager;
    private final HubConnectionJpaRepository jpaRepository;
    private final HubConnectionMapper mapper;

    @Override
    public boolean existsBySourceAndDestination(HubId sourceHubId, HubId destinationHubId) {
        return jpaRepository.existsBySourceHubIdAndDestinationHubId(sourceHubId.id(), destinationHubId.id());
    }

    @Override
    public HubConnection save(HubConnection hubConnection) {
        try{
            HubConnectionJpaEntity entity = jpaRepository.findById(hubConnection.getId().id())
                    .map(existing->{
                        mapper.updateJpaEntity(existing, hubConnection);
                        return existing;
                    })
                    .orElseGet(() -> mapper.toJpaEntity(hubConnection));
            HubConnectionJpaEntity savedEntity = jpaRepository.saveAndFlush(entity);
            evictAllCache();
            return mapper.toDomain(savedEntity);
        }catch (ObjectOptimisticLockingFailureException e) {
            throw new HubConnectionException(HubConnectionErrorCode.HUB_CONNECTION_CONFLICT);
        }
        catch (DataIntegrityViolationException e) {
                throw new HubConnectionException(HubConnectionErrorCode.DUPLICATE_HUB_CONNECTION);
        }
    }

    @Override
    public List<HubConnection> findAll() {
        HubConnectionCacheDto[] cached = getHubConnectionAllCache();
        if (cached != null) {
            return Arrays.stream(cached)
                    .map(HubConnectionCacheDto::toDomain)
                    .toList();
        }

        List<HubConnectionJpaEntity> entities = jpaRepository.findAll();
        List<HubConnection> hubConnections = entities.stream().map(mapper::toDomain).toList();
        putHubConnectionAllCache(hubConnections);
        return hubConnections;
    }

    @Override
    public HubConnection findById(HubConnectionId id) {
        HubConnectionJpaEntity hubConnection = jpaRepository.findById(id.id())
                .orElseThrow(()-> new HubConnectionException(HubConnectionErrorCode.HUB_CONNECTION_NOT_FOUND));

        return mapper.toDomain(hubConnection);
    }

    @Override
    public void delete(HubConnection hubConnection, UUID userId) {
        try {
            HubConnectionJpaEntity entity = jpaRepository.findById(hubConnection.getId().id())
                    .orElseThrow(() -> new HubConnectionException(HubConnectionErrorCode.HUB_CONNECTION_NOT_FOUND));

            entity.softDelete(userId);
            jpaRepository.flush();
            evictAllCache();
        }catch (ObjectOptimisticLockingFailureException e){
            throw new HubConnectionException(HubConnectionErrorCode.HUB_CONNECTION_CONFLICT);
        }
    }

    @Override
    public List<HubConnection> findAllByHubId(HubId id) {
        return jpaRepository.findAllBySourceHubIdOrDestinationHubId(id.id(), id.id()).stream()
                .map(mapper::toDomain)
                .toList();
    }


    private HubConnectionCacheDto[] getHubConnectionAllCache() {
        Cache cache = cacheManager.getCache(HUB_CONNECTION_ALL_CACHE);
        return cache != null ? cache.get(HUB_CONNECTION_ALL_KEY, HubConnectionCacheDto[].class) : null;
    }

    private void putHubConnectionAllCache(List<HubConnection> hubConnections) {
        Cache cache = cacheManager.getCache(HUB_CONNECTION_ALL_CACHE);
        if (cache != null) {
            cache.put(
                    HUB_CONNECTION_ALL_KEY,
                    hubConnections.stream()
                            .map(HubConnectionCacheDto::from)
                            .toArray(HubConnectionCacheDto[]::new)
            );
        }
    }

    private void evictAllCache() {
        Cache cache = cacheManager.getCache(HUB_CONNECTION_ALL_CACHE);
        if (cache != null) {
            cache.evict(HUB_CONNECTION_ALL_KEY);
        }
    }
}
