package com.firstlogistics.hubservice.domain.repository;

import com.firstlogistics.hubservice.domain.entity.Hub;

public interface HubRepository {

    boolean existsByHubName(String name);

    Hub save(Hub hub);
}
