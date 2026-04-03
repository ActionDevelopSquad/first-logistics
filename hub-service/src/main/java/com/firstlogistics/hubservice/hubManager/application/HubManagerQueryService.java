package com.firstlogistics.hubservice.hubManager.application;

import com.firstlogistics.hubservice.hubManager.application.dto.query.SearchHubManagersQuery;
import com.firstlogistics.hubservice.hubManager.application.dto.result.HubManagerResult;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubManagerQueryService {

    private final HubManagerQueryRepository repository;

    public HubManagerResult getHubManager(UUID hubManagerId){
        HubManager hubManager = repository.findById(hubManagerId);
        return HubManagerResult.from(hubManager);
    }

    public Page<HubManagerResult> searchHubManagers(SearchHubManagersQuery query, Pageable pageable){
        Page<HubManager> hubManagers = repository.searchByCondition(query.toSpec(), pageable);
        return hubManagers.map(HubManagerResult::from);
    }
    public UUID getHubIdByUserId(UUID userId){
        return repository.findHubIdByUserId(userId);
    }
}
