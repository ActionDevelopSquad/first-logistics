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
          hubConnection.getSourceHubId().id(),
          hubConnection.getDestinationHubId().id(),
          hubConnection.getTime().minutes(),
          hubConnection.getDistance().meters(),
          hubConnection.getStatus()
        );
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
