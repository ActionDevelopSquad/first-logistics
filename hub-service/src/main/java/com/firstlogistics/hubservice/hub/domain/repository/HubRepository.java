package com.firstlogistics.hubservice.hub.domain.repository;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;

public interface HubRepository {

    boolean existsByHubName(String name);

    Hub save(Hub hub);
}
