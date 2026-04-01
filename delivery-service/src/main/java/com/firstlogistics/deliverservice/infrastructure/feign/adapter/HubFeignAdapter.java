package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubFeignAdapter implements HubPort {

	private final HubClient hubClient;

	@Override
	public HubRouteResponse getHubRoute(UUID sourceHubId, UUID destinationHubId) {
		return hubClient.getHubRoute(sourceHubId, destinationHubId).data();
	}

	@Override
	public List<HubResponse> getHubs(List<UUID> hubIds) {
		return hubClient.getHubs(hubIds).data();
	}
}
