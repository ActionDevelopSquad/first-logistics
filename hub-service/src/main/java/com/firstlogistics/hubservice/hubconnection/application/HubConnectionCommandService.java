package com.firstlogistics.hubservice.hubconnection.application;

import com.firstlogistics.hubservice.hub.application.dto.command.CreateHubCommand;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.application.dto.command.CreateHubConnectionCommand;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubConnectionResult;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;
import org.springframework.stereotype.Service;

@Service
public class HubConnectionCommandService {

    public HubConnectionResult create(CreateHubConnectionCommand command) {
        HubId sourceHubId = HubId.of(command.sourceHubId());
        HubId destinationHubId = HubId.of(command.destinationHubId());
        Time time = Time.of(command.minutes());
        Distance distance = Distance.of(command.meters());

        HubConnection hubConnection = HubConnection.create(sourceHubId, destinationHubId, time, distance);

        return HubConnectionResult.from(hubConnection);
    }
}
