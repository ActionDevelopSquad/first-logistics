package com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerRepository;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubManagerRepositoryImpl implements HubManagerRepository {
    private final HubManagerJpaRepository jpaRepository;

    @Override
    public boolean existsByUserIdAndHubId(UserId userId, HubId hubId) {
        return jpaRepository.existsByUserIdAndHubId(userId.id(), hubId.id());
    }

    @Override
    public HubManager save(HubManager hubManager) {
        try {
            HubManagerJpaEntity savedEntity = jpaRepository.saveAndFlush(HubManagerMapper.toJpaEntity(hubManager));
            return HubManagerMapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
                throw new HubManagerException(HubManagerErrorCode.DUPLICATE_HUB_MANAGER);
        }
    }

    @Override
    public HubManager findById(HubManagerId id) {
        HubManagerJpaEntity hubManager = jpaRepository.findById(id.id())
                .orElseThrow(()-> new HubManagerException(HubManagerErrorCode.HUB_MANAGER_NOT_FOUND));

        return HubManagerMapper.toDomain(hubManager);
    }

    @Override
    public void delete(HubManager hubManager, UUID userId) {
        HubManagerJpaEntity entity = jpaRepository.findById(hubManager.getId().id())
                .orElseThrow(() -> new HubManagerException(HubManagerErrorCode.HUB_MANAGER_NOT_FOUND));
        entity.softDelete(userId);
        jpaRepository.saveAndFlush(entity);
    }
}
