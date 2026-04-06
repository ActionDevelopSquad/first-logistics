package com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.vo.GeoLocation;
import com.firstlogistics.hubservice.hub.domain.vo.HubAddress;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import org.springframework.stereotype.Component;

@Component
public class HubMapper {

    public HubJpaEntity toJpaEntity(Hub hub){
        return new HubJpaEntity(
                hub.getId().id(),
                hub.getName(),
                hub.getAddress().roadAddress(),
                hub.getGeoLocation().latitude(),
                hub.getGeoLocation().longitude(),
                hub.getStatus(),
                hub.getType()
        );
    }

    public Hub toDomain(HubJpaEntity jpaEntity){
        return Hub.reconstitute(
                HubId.of(jpaEntity.getId()),
                jpaEntity.getName(),
                HubAddress.of(jpaEntity.getRoadAddress()),
                GeoLocation.of(jpaEntity.getLatitude(), jpaEntity.getLongitude()),
                jpaEntity.getStatus(),
                jpaEntity.getType()
        );
    }
}
