package com.firstlogistics.hubservice.hubconnection.application;

import com.firstlogistics.hubservice.hub.application.HubQueryService;
import com.firstlogistics.hubservice.hub.application.dto.result.HubDetailsResult;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubRouteLegResult;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubRouteResult;
import com.firstlogistics.hubservice.hubconnection.application.port.CompanyPort;
import com.firstlogistics.hubservice.hubconnection.application.port.RouteMetricPort;
import com.firstlogistics.hubservice.hubconnection.application.port.RoutePolicyProvider;
import com.firstlogistics.hubservice.hubconnection.application.port.dto.CompanyResponse;
import com.firstlogistics.hubservice.hubconnection.application.port.dto.RouteMetricResponse;
import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import com.firstlogistics.hubservice.hubconnection.domain.service.HubRouteDomainService;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubRouteQueryService {
    private final HubRouteDomainService hubRouteDomainService;
    private final RoutePolicyProvider policyProvider;
    private final CompanyPort companyPort;
    private final RouteMetricPort routeMetricPort;
    private final HubQueryService hubQueryService;

    public HubRouteResult getRoute(UUID sourceHubId, UUID destinationHubId, UUID destinationCompanyId){

        return buildRoute(
                HubId.of(sourceHubId),
                HubId.of(destinationHubId),
                destinationCompanyId,
                policyProvider.getDefaultPolicy()
        );
    }
    public HubRouteResult getRoute(UUID sourceHubId, UUID destinationHubId, UUID destinationCompanyId, String policy){
        return buildRoute(
                HubId.of(sourceHubId),
                HubId.of(destinationHubId),
                destinationCompanyId,
                RoutePolicy.from(policy)
        );
    }


    private HubRouteResult buildRoute(
            HubId sourceHubId,
            HubId destinationHubId,
            UUID destinationCompanyId,
            RoutePolicy policy
    ) {
        HubRoute hubRoute = hubRouteDomainService.calculateRoute(
                sourceHubId,
                destinationHubId,
                destinationCompanyId,
                policy
        );

        List<HubRouteLegResult> routeLegResults = new ArrayList<>(
                hubRoute.routes().stream()
                        .map(HubRouteLegResult::from)
                        .toList()
        );

        HubRouteLegResult lastHubRouteLeg = createLastHubRouteLeg(
                routeLegResults.size() + 1,
                destinationHubId,
                destinationCompanyId
        );

        routeLegResults.add(lastHubRouteLeg);

        return HubRouteResult.of(hubRoute, lastHubRouteLeg, routeLegResults);
    }

    private HubRouteLegResult createLastHubRouteLeg(int sequence, HubId destinationHubId, UUID destinationCompanyId){
        CompanyResponse company = companyPort.getCompany(destinationCompanyId);
        HubDetailsResult hub = hubQueryService.getHub(destinationHubId.id());

        RouteMetricResponse metricResponse = routeMetricPort.getMetrics(hub.latitude(),hub.longitude(),company.latitude(), company.longitude());

        return HubRouteLegResult.of(sequence,destinationHubId.id(),destinationCompanyId,metricResponse.durationMinutes(), metricResponse.distanceMeters());
    }
}
