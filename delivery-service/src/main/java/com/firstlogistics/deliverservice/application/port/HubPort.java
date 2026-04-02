package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;

import java.util.List;
import java.util.UUID;

public interface HubPort {

	HubRouteResponse getHubRoute(UUID sourceHubId, UUID destinationHubId);

	List<HubResponse> getHubs(List<UUID> hubIds);
}
