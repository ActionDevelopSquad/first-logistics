package com.firstlogistics.hubservice.hub.domain.repository;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;

public interface HubRepository {

    boolean existsByHubName(String name);

    Hub save(Hub hub);

    boolean existsByHubId(HubId hubId);

    Hub findById(HubId hubId);

    void delete(Hub hub);
}
