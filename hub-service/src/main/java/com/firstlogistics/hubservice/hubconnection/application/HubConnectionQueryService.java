package com.firstlogistics.hubservice.hubconnection.application;

import com.firstlogistics.hubservice.hubconnection.application.dto.query.SearchHubConnectionQuery;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubConnectionResult;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionQueryRepository;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubConnectionQueryService {
    private final HubConnectionQueryRepository queryRepository;

    public HubConnectionResult getHubConnection(UUID hubConnectionId){
        HubConnection hubConnection = queryRepository.findById(HubConnectionId.of(hubConnectionId));
        return HubConnectionResult.from(hubConnection);
    }
    public Page<HubConnectionResult> searchHubConnection(SearchHubConnectionQuery query, Pageable pageable){
        Page<HubConnection> hubConnections = queryRepository.searchByCondition(query.toSpec(),pageable);
        return hubConnections.map(HubConnectionResult::from);
    }

}
