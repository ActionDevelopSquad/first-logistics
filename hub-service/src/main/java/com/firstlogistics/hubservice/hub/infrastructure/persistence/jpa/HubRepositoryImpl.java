package com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<Hub> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Hub findById(HubId hubId) {
        HubJpaEntity entity = jpaRepository.findById(hubId.id())
                .orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));
        return mapper.toDomain(entity);
    }

    private boolean hasConstraintName(Throwable throwable, String expectedConstraintName ){
        Throwable cause = throwable;
        while(cause!=null){
            if(cause instanceof  org.hibernate.exception.ConstraintViolationException cve){
                String constraintName = cve.getConstraintName();
                if(expectedConstraintName.equalsIgnoreCase(constraintName))
                    return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}

