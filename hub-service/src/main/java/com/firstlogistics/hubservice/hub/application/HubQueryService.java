package com.firstlogistics.hubservice.hub.application;

import com.firstlogistics.hubservice.hub.application.dto.query.SearchHubsQuery;
import com.firstlogistics.hubservice.hub.application.dto.result.HubDetailsResult;
import com.firstlogistics.hubservice.hub.application.dto.result.SearchHubResult;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubQueryRepository;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubDetailsDto;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubPageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubQueryService {
    private static final double MIN_KOREA_LATITUDE = 30.0;
    private static final double MAX_KOREA_LATITUDE = 45.0;
    private static final double MIN_KOREA_LONGITUDE = 120.0;
    private static final double MAX_KOREA_LONGITUDE = 135.0;

    private final HubQueryRepository repository;

    public HubDetailsResult getHub(UUID hubId){
        HubDetailsDto hub = repository.findById(hubId);
        return HubDetailsResult.from(hub);
    }

    public Page<SearchHubResult> searchHubs(SearchHubsQuery query, Pageable pageable){
        Page<HubPageDto> hubs = repository.searchByCondition(query.toDto(),pageable);
        return hubs.map(SearchHubResult::from);
    }

    public UUID getNearestHub(double latitude, double longitude){
        validateServiceArea(latitude, longitude);
        return repository.findNearest(latitude, longitude);
    }

    private  void validateServiceArea(double latitude, double longitude){
        if(latitude< MIN_KOREA_LATITUDE || latitude >MAX_KOREA_LATITUDE || longitude <MIN_KOREA_LONGITUDE || longitude > MAX_KOREA_LONGITUDE)
            throw new HubException(HubErrorCode.INVALID_SERVICE_AREA);
    }
}
