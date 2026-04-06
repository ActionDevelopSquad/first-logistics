package com.firstlogistics.hubservice.hubconnection.application;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubRouteLegResult;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubRouteResult;
import com.firstlogistics.hubservice.hubconnection.application.port.CompanyPort;
import com.firstlogistics.hubservice.hubconnection.application.port.RouteMetricPort;
import com.firstlogistics.hubservice.hubconnection.application.port.RoutePolicyProvider;
import com.firstlogistics.hubservice.hubconnection.application.port.dto.CompanyResponse;
import com.firstlogistics.hubservice.hubconnection.application.port.dto.RouteMetricResponse;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import com.firstlogistics.hubservice.hubconnection.domain.service.HubRouteDomainService;
import com.firstlogistics.hubservice.hubconnection.domain.vo.CompanyId;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;
import com.firstlogistics.hubservice.hubconnection.domain.vo.RouteHub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubRouteQueryService {
    private final HubRouteDomainService hubRouteDomainService;
    private final RoutePolicyProvider policyProvider;
    private final CompanyPort companyPort;
    private final RouteMetricPort routeMetricPort;
    private final HubRepository hubRepository;
    private final HubConnectionRepository hubConnectionRepository;

    public HubRouteResult getRoute(UUID sourceHubId,UUID destinationCompanyId, String policy){
        CompanyId companyId = CompanyId.of(destinationCompanyId);
        CompanyResponse company = companyPort.getCompany(companyId);

        return buildRoute(
                HubId.of(sourceHubId),
                HubId.of(company.hubId()),
                company,
                resolvePolicy(policy)
        );
    }



    private HubRouteResult buildRoute(
            HubId sourceHubId,
            HubId destinationHubId,
            CompanyResponse destinationCompany,
            RoutePolicy policy
    ) {
        List<HubConnection> connections = hubConnectionRepository.findAll();
        Map<HubId, RouteHub> hubMap = hubRepository.findAll().stream().collect(Collectors.toMap(Hub::getId, RouteHub::from));

        HubRoute hubRoute = hubRouteDomainService.calculateRoute(
                sourceHubId,
                destinationHubId,
                policy,
                hubMap,
                connections
        );

        List<HubRouteLegResult> routeLegResults = new ArrayList<>(
                hubRoute.routes().stream()
                        .map(HubRouteLegResult::from)
                        .toList()
        );

        HubRouteLegResult lastHubRouteLeg = createLastHubRouteLeg(
                routeLegResults.size() + 1,
                destinationHubId,
                destinationCompany
        );

        routeLegResults.add(lastHubRouteLeg);

        return HubRouteResult.of(hubRoute, lastHubRouteLeg, routeLegResults);
    }

    private HubRouteLegResult createLastHubRouteLeg(int sequence, HubId destinationHubId, CompanyResponse company){
        Hub hub = hubRepository.findById(destinationHubId);
        RouteMetricResponse metricResponse = routeMetricPort.getMetrics(hub.getGeoLocation().latitude(),hub.getGeoLocation().longitude(),company.latitude(), company.longitude());

        return HubRouteLegResult.of(sequence,destinationHubId.id(),company.companyId(),metricResponse.durationMinutes(), metricResponse.distanceMeters());
    }

    private RoutePolicy resolvePolicy(String policy){
        return (policy == null || policy.isBlank())
                        ? policyProvider.getDefaultPolicy()
                        : RoutePolicy.from(policy);
    }
}
