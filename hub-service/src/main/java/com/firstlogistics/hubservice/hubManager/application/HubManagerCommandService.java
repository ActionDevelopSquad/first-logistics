package com.firstlogistics.hubservice.hubManager.application;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerRepository;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubManagerCommandService {
    private final HubManagerRepository hubManagerRepository;

    @Transactional
    public void createHubManager(UUID userId, UUID hubId){
        UserId user = UserId.of(userId);
        HubId hub = HubId.of(hubId);
        if(hubManagerRepository.existsByUserIdAndHubId(user,hub)){
            throw new HubManagerException(HubManagerErrorCode.DUPLICATE_HUB_MANAGER);
        }
        HubManager hubManager = HubManager.create(user, hub);
        hubManagerRepository.save(hubManager);
    }
}
