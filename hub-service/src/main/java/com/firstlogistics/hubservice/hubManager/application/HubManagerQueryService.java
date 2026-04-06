package com.firstlogistics.hubservice.hubManager.application;

import com.firstlogistics.hubservice.hubManager.application.dto.query.SearchHubManagersQuery;
import com.firstlogistics.hubservice.hubManager.application.dto.result.HubManagerResult;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerQueryRepository;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import common.security.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubManagerQueryService {

    private final HubManagerQueryRepository repository;

    public HubManagerResult getHubManager(UUID hubManagerId, UUID userId, UserRole userRole){
        HubManager hubManager = repository.findById(HubManagerId.of(hubManagerId));
        validateHubManager(hubManager, userId, userRole);
        return HubManagerResult.from(hubManager);
    }

    public Page<HubManagerResult> searchHubManagers(SearchHubManagersQuery query, Pageable pageable){
        Page<HubManager> hubManagers = repository.searchByCondition(query.toSpec(), pageable);
        return hubManagers.map(HubManagerResult::from);
    }
    public HubManagerResult getHubManagerByUserId(UUID userId){
        HubManager hubManager =  repository.findByUserId(UserId.of(userId));
        return HubManagerResult.from(hubManager);
    }

    private void validateHubManager(HubManager hubManager, UUID userId, UserRole userRole){
        if(userRole== UserRole.HUB_MANAGER)
            if(!hubManager.getUserId().id().equals(userId))
                throw new HubManagerException(HubManagerErrorCode.FORBIDDEN_HUB_MANAGER_ACCESS);
    }
}
