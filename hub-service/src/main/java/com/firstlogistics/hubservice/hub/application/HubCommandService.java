package com.firstlogistics.hubservice.hub.application;

import com.firstlogistics.hubservice.hub.application.dto.command.CreateHubCommand;
import com.firstlogistics.hubservice.hub.application.dto.result.HubResult;
import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.GeoLocation;
import com.firstlogistics.hubservice.hub.domain.vo.HubAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubCommandService {

    private final HubRepository hubRepository;

    @Transactional
    public HubResult create(CreateHubCommand command){
        if(hubRepository.existsByHubName(command.name()))
            throw new HubException(HubErrorCode.DUPLICATE_HUB_NAME);

        Hub hub = Hub.create(
                command.name(),
                HubAddress.of(command.roadAddress()),
                GeoLocation.of(command.latitude(), command.longitude()),
                HubType.from(command.type())
        );

        hubRepository.save(hub);

        return HubResult.from(hub);
    }
}
