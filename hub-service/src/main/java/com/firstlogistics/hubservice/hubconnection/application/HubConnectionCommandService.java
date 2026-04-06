package com.firstlogistics.hubservice.hubconnection.application;

import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.application.dto.command.ChangeHubConnectionStatusCommand;
import com.firstlogistics.hubservice.hubconnection.application.dto.command.CreateHubConnectionCommand;
import com.firstlogistics.hubservice.hubconnection.application.dto.command.UpdateHubConnectionCommand;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubConnectionResult;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class HubConnectionCommandService {
    private final HubConnectionRepository hubConnectionRepository;
    private final HubRepository hubRepository;

    @Transactional
    public HubConnectionResult create(CreateHubConnectionCommand command) {
        HubId sourceHubId = HubId.of(command.sourceHubId());
        HubId destinationHubId = HubId.of(command.destinationHubId());
        Time time = Time.of(command.minutes());
        Distance distance = Distance.of(command.meters());

        validateHubExists(sourceHubId);
        validateHubExists(destinationHubId);
        validateHubConnectionExists(sourceHubId, destinationHubId);

        HubConnection hubConnection = HubConnection.create(sourceHubId, destinationHubId, time, distance);

        HubConnection savedHubConnection = hubConnectionRepository.save(hubConnection);

        return HubConnectionResult.from(savedHubConnection);
    }

    @Transactional
    public HubConnectionResult update(UUID hubConnectionId, UpdateHubConnectionCommand command){
        HubConnection connection = hubConnectionRepository.findById(HubConnectionId.of(hubConnectionId));

        if (command.minutes() != null) {
            connection.changeTime(Time.of(command.minutes()));
        }
        if (command.meters() != null) {
            connection.changeDistance(Distance.of(command.meters()));
        }

        HubConnection savedHubConnection = hubConnectionRepository.save(connection);
        return HubConnectionResult.from(savedHubConnection);
    }

    @Transactional
    public HubConnectionResult changeStatus(UUID hubConnectionId, ChangeHubConnectionStatusCommand command){
        if(command.status() == HubConnectionStatus.ACTIVE)
            return activate(hubConnectionId);
        if(command.status() == HubConnectionStatus.INACTIVE)
            return deactivate(hubConnectionId);

        throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_STATUS);
    }

    @Transactional
    public void delete(UUID hubConnectionId){
        HubConnection connection = hubConnectionRepository.findById(HubConnectionId.of(hubConnectionId));
        hubConnectionRepository.delete(connection);
    }

    @Transactional
    public void deactivateByHub(UUID hubId) {
        hubConnectionRepository.deactivateByHubId(HubId.of(hubId));
    }

    @Transactional
    public void activateByHub(UUID hubId) {
        hubConnectionRepository.activateByHubId(HubId.of(hubId));

    }

    @Transactional
    public void deleteByHub(UUID hubId) {
        hubConnectionRepository.deleteByHubId(HubId.of(hubId));
    }

    private HubConnectionResult activate(UUID hubConnectionId) {
        HubConnection connection = hubConnectionRepository.findById(HubConnectionId.of(hubConnectionId));
        connection.activate();

        HubConnection savedHubConnection = hubConnectionRepository.save(connection);
        return HubConnectionResult.from(savedHubConnection);
    }

    private HubConnectionResult deactivate(UUID hubConnectionId) {
        HubConnection connection = hubConnectionRepository.findById(HubConnectionId.of(hubConnectionId));
        connection.deactivate();

        HubConnection savedHubConnection = hubConnectionRepository.save(connection);
        return HubConnectionResult.from(savedHubConnection);
    }

    private void validateHubExists(HubId id){
        if(!hubRepository.existsByHubId(id))
            throw new HubConnectionException(HubConnectionErrorCode.HUB_NOT_FOUND);
    }

    private void validateHubConnectionExists(HubId sourceHubId, HubId destinationHubId){
        if(hubConnectionRepository.existsBySourceAndDestination(sourceHubId,destinationHubId))
            throw new HubConnectionException(HubConnectionErrorCode.DUPLICATE_HUB_CONNECTION);
    }
}
