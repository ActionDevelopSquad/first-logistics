package com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;

public class HubManagerMapper {
    public static HubManagerJpaEntity toJpaEntity(HubManager hubManager){
        return new HubManagerJpaEntity(
                hubManager.getId().id(),
                hubManager.getUserId().id(),
                hubManager.getHubId().id()
        );
    }


    public static HubManager toDomain(HubManagerJpaEntity jpaEntity){
        return HubManager.reconstitute(
                HubManagerId.of(jpaEntity.getId()),
                UserId.of(jpaEntity.getUserId()),
                HubId.of(jpaEntity.getHubId())
        );
    }
}
