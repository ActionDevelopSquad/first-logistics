package com.firstlogistics.hubservice.hubconnection.application;

import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.application.dto.command.CreateHubConnectionCommand;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubConnectionResult;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HubConnectionCommandService {
    private final HubConnectionRepository hubConnectionRepository;
    private final HubRepository hubRepository;

    public HubConnectionResult create(CreateHubConnectionCommand command) {
        HubId sourceHubId = HubId.of(command.sourceHubId());
        HubId destinationHubId = HubId.of(command.destinationHubId());
        Time time = Time.of(command.minutes());
        Distance distance = Distance.of(command.meters());

        validateHubExists(sourceHubId);
        validateHubExists(destinationHubId);
        validateHubConnectionExists(sourceHubId, destinationHubId);

        HubConnection hubConnection = HubConnection.create(sourceHubId, destinationHubId, time, distance);

        return HubConnectionResult.from(hubConnection);
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
