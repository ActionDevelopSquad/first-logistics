package com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hub.infrastructure.cache.dto.HubCacheDto;
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
public class HubRepositoryImpl implements HubRepository {
    private static final String HUB_ALL_CACHE = "hub:all";
    private static final String HUB_ALL_KEY = "all";
    private static final String HUB_BY_ID_CACHE = "hub:byId";

    private final CacheManager cacheManager;
    private final HubJpaRepository jpaRepository;
    private final HubMapper mapper;

    @Override
    public boolean existsByHubName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public Hub save(Hub hub) {
        try {
            HubJpaEntity entity = jpaRepository.findById(hub.getId().id())
                    .map(existing -> {
                        mapper.updateJpaEntity(existing, hub);
                        return existing;
                    })
                    .orElseGet(() -> mapper.toJpaEntity(hub));

            Hub savedHub = mapper.toDomain(jpaRepository.saveAndFlush(entity));
            putHubByIdCache(savedHub);
            evictHubAllCache();
            return savedHub;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new HubException(HubErrorCode.HUB_CONFLICT);
        } catch (DataIntegrityViolationException e) {
                throw new HubException(HubErrorCode.DUPLICATE_HUB_NAME);
        }
    }

    @Override
    public boolean existsByHubId(HubId hubId) {
        return jpaRepository.existsById(hubId.id());
    }

    @Override
    public List<Hub> findAll() {
        HubCacheDto[] cached = getHubAllCache();
        if (cached != null) {
            return Arrays.stream(cached)
                    .map(HubCacheDto::toDomain)
                    .toList();
        }

        List<Hub> hubs = jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
        putHubAllCache(hubs);
        return hubs;
    }

    @Override
    public Hub findById(HubId hubId) {
        HubCacheDto cached = getHubByIdCache(hubId);
        if (cached != null) {
            return cached.toDomain();
        }

        Hub hub = jpaRepository.findById(hubId.id())
                .map(mapper::toDomain)
                .orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));
        putHubByIdCache(hub);
        return hub;
    }

    @Override
    public void delete(Hub hub, UUID userId) {
        try {
            HubJpaEntity entity = jpaRepository.findById(hub.getId().id())
                    .orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));

            entity.softDelete(userId);
            jpaRepository.flush();
            evictHubByIdCache(hub.getId());
            evictHubAllCache();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new HubException(HubErrorCode.HUB_CONFLICT);
        }
    }

    private HubCacheDto getHubByIdCache(HubId hubId) {
        Cache cache = cacheManager.getCache(HUB_BY_ID_CACHE);
        return cache != null ? cache.get(hubId.id(), HubCacheDto.class) : null;
    }

    private HubCacheDto[] getHubAllCache() {
        Cache cache = cacheManager.getCache(HUB_ALL_CACHE);
        return cache != null ? cache.get(HUB_ALL_KEY, HubCacheDto[].class) : null;
    }

    private void putHubByIdCache(Hub hub) {
        Cache cache = cacheManager.getCache(HUB_BY_ID_CACHE);
        if (cache != null) {
            cache.put(hub.getId().id(), HubCacheDto.from(hub));
        }
    }

    private void putHubAllCache(List<Hub> hubs) {
        Cache cache = cacheManager.getCache(HUB_ALL_CACHE);
        if (cache != null) {
            cache.put(HUB_ALL_KEY, hubs.stream().map(HubCacheDto::from).toArray(HubCacheDto[]::new));
        }
    }

    private void evictHubByIdCache(HubId hubId) {
        Cache cache = cacheManager.getCache(HUB_BY_ID_CACHE);
        if (cache != null) {
            cache.evict(hubId.id());
        }
    }

    private void evictHubAllCache() {
        Cache cache = cacheManager.getCache(HUB_ALL_CACHE);
        if (cache != null) {
            cache.evict(HUB_ALL_KEY);
        }
    }

}
