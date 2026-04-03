package com.firstlogistics.hubservice.hubManager.domain.repository;


import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.specification.HubManagerSearchSpec;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubManagerQueryRepository {
    HubManager findById(HubManagerId hubManagerId);

    Page<HubManager> searchByCondition(HubManagerSearchSpec spec, Pageable pageable);

    UUID findHubIdByUserId(UserId userId);
}
