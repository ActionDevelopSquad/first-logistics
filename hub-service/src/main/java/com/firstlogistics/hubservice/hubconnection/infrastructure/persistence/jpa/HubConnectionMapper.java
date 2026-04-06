package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;
import org.springframework.stereotype.Component;

@Component
public class HubConnectionMapper {
    public HubConnectionJpaEntity toJpaEntity(HubConnection hubConnection){
        return new HubConnectionJpaEntity(
          hubConnection.getId().id(),
          null,
          hubConnection.getSourceHubId().id(),
          hubConnection.getDestinationHubId().id(),
          hubConnection.getTime().minutes(),
          hubConnection.getDistance().meters(),
          hubConnection.getStatus()
        );
    }

    public void updateJpaEntity(HubConnectionJpaEntity jpaEntity, HubConnection hubConnection) {
        jpaEntity.changeSourceHubId(hubConnection.getSourceHubId().id());
        jpaEntity.changeDestinationHubId(hubConnection.getDestinationHubId().id());
        jpaEntity.changeTime(hubConnection.getTime().minutes());
        jpaEntity.changeDistance(hubConnection.getDistance().meters());
        jpaEntity.changeStatus(hubConnection.getStatus());
    }


    public HubConnection toDomain(HubConnectionJpaEntity jpaEntity){
        return HubConnection.reconstitute(
                HubConnectionId.of(jpaEntity.getId()),
                HubId.of(jpaEntity.getSourceHubId()),
                HubId.of(jpaEntity.getDestinationHubId()),
                Time.of(jpaEntity.getMinutes()),
                Distance.of(jpaEntity.getMeters()),
                jpaEntity.getStatus()
        );
    }
}
