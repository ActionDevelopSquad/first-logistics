package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.query;

import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionQueryRepository;
import com.firstlogistics.hubservice.hubconnection.domain.specification.HubConnectionSpec;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubConnectionQueryRepositoryImpl implements HubConnectionQueryRepository {
    @Override
    public HubConnection findById(HubConnectionId id) {
        return null;
    }

    @Override
    public Page<HubConnection> searchByCondition(HubConnectionSpec spec) {
        return null;
    }
}
