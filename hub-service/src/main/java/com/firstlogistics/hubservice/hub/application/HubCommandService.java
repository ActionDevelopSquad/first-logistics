package com.firstlogistics.hubservice.hub.application;

import com.firstlogistics.hubservice.hub.application.dto.command.ChangeHubStatusCommand;
import com.firstlogistics.hubservice.hub.application.dto.command.CreateHubCommand;
import com.firstlogistics.hubservice.hub.application.dto.command.UpdateHubCommand;
import com.firstlogistics.hubservice.hub.application.dto.result.HubResult;
import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.event.HubActivatedEvent;
import com.firstlogistics.hubservice.hub.domain.event.HubDeactivatedEvent;
import com.firstlogistics.hubservice.hub.domain.event.HubDeletedEvent;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.GeoLocation;
import com.firstlogistics.hubservice.hub.domain.vo.HubAddress;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubCommandService {

    private final HubRepository hubRepository;

    @Transactional
    public HubResult create(CreateHubCommand command) {
        if (hubRepository.existsByHubName(command.name()))
            throw new HubException(HubErrorCode.DUPLICATE_HUB_NAME);

        Hub hub = Hub.create(
                command.name(),
                HubAddress.of(command.roadAddress()),
                GeoLocation.of(command.latitude(), command.longitude())
        );

        hubRepository.save(hub);

        return HubResult.from(hub);
    }

    @Transactional
    public HubResult update(UUID hubId, UpdateHubCommand command) {
        Hub hub = hubRepository.findById(HubId.of(hubId));

        if (command.name() != null && !hub.getName().equals(command.name())) {
            if (hubRepository.existsByHubName(command.name())) {
                throw new HubException(HubErrorCode.DUPLICATE_HUB_NAME);
            }
            hub.changeName(command.name());

        }
        if (command.roadAddress() != null) {
            hub.changeAddress(HubAddress.of(command.roadAddress()));
        }

        Hub savedHub = hubRepository.save(hub);
        return HubResult.from(savedHub);
    }

    @Transactional
    public HubResult changeStatus(UUID hubId, ChangeHubStatusCommand command){
        if(command.status() == HubStatus.ACTIVE)
            return activate(hubId);
        if(command.status() == HubStatus.INACTIVE)
            return deactivate(hubId);

        throw new HubException(HubErrorCode.INVALID_HUB_STATUS);
    }

    @Transactional
    public void delete(UUID hubId) {
        Hub hub = hubRepository.findById(HubId.of(hubId));
        hubRepository.delete(hub);
        Events.trigger(HubDeletedEvent.from(hub));
    }

    private HubResult activate(UUID hubId) {
        Hub hub = hubRepository.findById(HubId.of(hubId));
        hub.activate();

        Hub savedHub = hubRepository.save(hub);
        Events.trigger(HubActivatedEvent.from(savedHub));
        return HubResult.from(savedHub);
    }

    private HubResult deactivate(UUID hubId) {
        Hub hub = hubRepository.findById(HubId.of(hubId));
        hub.deactivate();

        Hub savedHub = hubRepository.save(hub);
        Events.trigger(HubDeactivatedEvent.from(savedHub));
        return HubResult.from(savedHub);
    }
}
