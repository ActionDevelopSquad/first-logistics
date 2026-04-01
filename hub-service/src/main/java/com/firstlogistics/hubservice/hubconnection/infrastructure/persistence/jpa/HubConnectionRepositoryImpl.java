package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubConnectionRepositoryImpl implements HubConnectionRepository {
    private final HubConnectionJpaRepository jpaRepository;
    private final HubConnectionMapper mapper;

    @Override
    public boolean existsBySourceAndDestination(HubId sourceHubId, HubId destinationHubId) {
        return jpaRepository.existsBySourceHubIdAndDestinationHubId(sourceHubId.id(), destinationHubId.id());
    }

    @Override
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
