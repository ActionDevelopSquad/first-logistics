package com.firstlogistics.hubservice.hubManager.application;

import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.application.dto.command.CreateHubManagerCommand;
import com.firstlogistics.hubservice.hubManager.application.dto.command.UpdateHubManagerCommand;
import com.firstlogistics.hubservice.hubManager.application.dto.result.HubManagerResult;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerRepository;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class HubManagerCommandService {
    private final HubManagerRepository hubManagerRepository;
    private final HubRepository hubRepository;

    @Transactional
    public void createHubManager(CreateHubManagerCommand command){
        UserId user = UserId.of(command.userId());
        HubId hub = HubId.of(command.hubId());
        if(!hubRepository.existsByHubId(hub))
            throw new HubManagerException(HubManagerErrorCode.HUB_NOT_FOUND);
        if(hubManagerRepository.existsByUserIdAndHubId(user,hub)){
            throw new HubManagerException(HubManagerErrorCode.DUPLICATE_HUB_MANAGER);
        }
        HubManager hubManager = HubManager.create(user, hub);
        hubManagerRepository.save(hubManager);
    }

    @Transactional
    public HubManagerResult updateHubManager(UUID hubManagerId, UpdateHubManagerCommand command){
        HubManager hubManager = hubManagerRepository.findById(HubManagerId.of(hubManagerId));
        HubId newHubId = HubId.of(command.hubId());

        validateSameHub(hubManager, newHubId);
        validateHubExists(newHubId);
        hubManager.changeHub(newHubId);

        HubManager savedHubManager = hubManagerRepository.save(hubManager);
        return HubManagerResult.from(savedHubManager);
    }

    @Transactional
    public void deleteHubManager(UUID hubManagerId,UUID userId){
        HubManager hubManager = hubManagerRepository.findById(HubManagerId.of(hubManagerId));
        hubManagerRepository.delete(hubManager, userId);
    }

    private void validateSameHub(HubManager hubManager, HubId hubId){
        if (hubManager.getHubId().equals(hubId)) {
            throw new HubManagerException(HubManagerErrorCode.SAME_HUB_MANAGER_HUB);
        }
    }

    private void validateHubExists(HubId hubId){
        if (!hubRepository.existsByHubId(hubId)) {
            throw new HubManagerException(HubManagerErrorCode.HUB_NOT_FOUND);
        }
    }
}
