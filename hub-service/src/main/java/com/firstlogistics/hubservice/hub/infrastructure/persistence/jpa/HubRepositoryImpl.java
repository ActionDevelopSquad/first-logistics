package com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {
    private static final String HUB_ALL_CACHE = "hub:all";
    private static final String HUB_BY_ID_CACHE = "hub:byId";

    private final HubJpaRepository jpaRepository;
    private final HubMapper mapper;

    @Override
    public boolean existsByHubName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    @Caching(
            put = {
                    @CachePut(cacheNames = HUB_BY_ID_CACHE, key = "#result.getId().id()")
            },
            evict = {
                    @CacheEvict(cacheNames = HUB_ALL_CACHE, allEntries = true)
            }
    )
    public Hub save(Hub hub) {
        try {
            HubJpaEntity savedEntity = jpaRepository.save(mapper.toJpaEntity(hub));
            return mapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            if (hasConstraintName(e, HubConstraints.UK_HUB_NAME))
                throw new HubException(HubErrorCode.DUPLICATE_HUB_NAME);
            throw e;
        }
    }

    @Override
    public boolean existsByHubId(HubId hubId) {
        return jpaRepository.existsById(hubId.id());
    }

    @Override
    @Cacheable(cacheNames = HUB_ALL_CACHE)
    public List<Hub> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Cacheable(cacheNames = HUB_BY_ID_CACHE, key = "#hubId.id()")
    public Hub findById(HubId hubId) {
        HubJpaEntity entity = jpaRepository.findById(hubId.id())
                .orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));
        return mapper.toDomain(entity);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = HUB_BY_ID_CACHE, key = "#hub.getId().id()"),
            @CacheEvict(cacheNames = HUB_ALL_CACHE, allEntries = true)
    })
    public void delete(Hub hub) {
        jpaRepository.delete(mapper.toJpaEntity(hub));
    }

    private boolean hasConstraintName(Throwable throwable, String expectedConstraintName) {
        Throwable cause = throwable;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
                String constraintName = cve.getConstraintName();
                if (expectedConstraintName.equalsIgnoreCase(constraintName))
                    return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}

