package com.firstlogistics.hubservice.hubManager.domain.repository;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;

import java.util.UUID;

public interface HubManagerRepository {
    boolean existsByUserIdAndHubId(UserId userId, HubId hubId);
    HubManager save(HubManager hubManager);
    HubManager findById(HubManagerId id);
    void delete(HubManager hubManager, UUID userId);
}
