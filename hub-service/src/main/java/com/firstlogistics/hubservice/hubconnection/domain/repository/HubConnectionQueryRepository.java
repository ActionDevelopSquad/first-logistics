package com.firstlogistics.hubservice.hubconnection.domain.repository;

import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.specification.HubConnectionSpec;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubConnectionQueryRepository {
    HubConnection findById(HubConnectionId id);

    Page<HubConnection> searchByCondition(HubConnectionSpec spec, Pageable pageable);
}
