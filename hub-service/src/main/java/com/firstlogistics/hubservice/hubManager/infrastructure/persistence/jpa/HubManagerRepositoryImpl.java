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
            if (hasConstraintName(e, HubManagerConstraints.UK_HUB_MANAGER_USER_HUB)|| hasConstraintName(e, HubManagerConstraints.UK_HUB_MANAGER_USER))
                throw new HubManagerException(HubManagerErrorCode.DUPLICATE_HUB_MANAGER);
            throw e;
        }
    }

    @Override
    public HubManager findById(HubManagerId id) {
        HubManagerJpaEntity hubManager = jpaRepository.findById(id.id())
                .orElseThrow(()-> new HubManagerException(HubManagerErrorCode.HUB_MANAGER_NOT_FOUND));

        return HubManagerMapper.toDomain(hubManager);
    }

    @Override
    public void delete(HubManager hubManager) {
        jpaRepository.delete(HubManagerMapper.toJpaEntity(hubManager));
    }


    private boolean hasConstraintName(Throwable throwable, String expectedConstraintName) {
        Throwable cause = throwable;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
                String constraintName = cve.getConstraintName();
                if (expectedConstraintName.equalsIgnoreCase(constraintName)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }
}
