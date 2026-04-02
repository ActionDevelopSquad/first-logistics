package com.firstlogistics.hubservice.hub.domain.repository;

import com.firstlogistics.hubservice.hub.domain.repository.dto.HubDetailsDto;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubSearchDto;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubSummaryDto;
import com.firstlogistics.hubservice.hub.domain.specification.HubIdsSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HubQueryRepository {
    HubDetailsDto findById(UUID hubId);

    Page<HubSummaryDto> searchByCondition(HubSearchDto hubSearchDto, Pageable pageable);

    UUID findNearest(double latitude, double longitude);

    List<HubSummaryDto> findAllByIds(HubIdsSpec spec);
}
