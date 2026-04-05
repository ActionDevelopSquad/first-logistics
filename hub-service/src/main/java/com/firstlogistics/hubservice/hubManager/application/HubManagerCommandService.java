package com.firstlogistics.hubservice.hubManager.application;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.application.dto.command.CreateHubManagerCommand;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerRepository;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class HubManagerCommandService {
    private final HubManagerRepository hubManagerRepository;

    @Transactional
    public void createHubManager(CreateHubManagerCommand command){
        UserId user = UserId.of(command.userId());
        HubId hub = HubId.of(command.hubId());
        if(hubManagerRepository.existsByUserIdAndHubId(user,hub)){
            throw new HubManagerException(HubManagerErrorCode.DUPLICATE_HUB_MANAGER);
        }
        HubManager hubManager = HubManager.create(user, hub);
        hubManagerRepository.save(hubManager);
    }
}
