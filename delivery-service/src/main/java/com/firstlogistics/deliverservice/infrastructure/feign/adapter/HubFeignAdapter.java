package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubFeignAdapter implements HubPort {

	private final HubClient hubClient;

	@Override
	public HubRouteResponse getHubRoute(UUID sourceHubId, UUID destinationHubId, UUID receiverCompanyId) {
		log.info("[Feign] hub-service 경로 조회 - sourceHubId: {}, destHubId: {}, receiverCompanyId: {}", sourceHubId, destinationHubId, receiverCompanyId);
		return hubClient.getHubRoute(sourceHubId, destinationHubId, receiverCompanyId).data();
	}

	@Override
	public List<HubResponse> getHubs(List<UUID> hubIds) {
		log.info("[Feign] hub-service 허브 목록 조회 - hubIds: {}", hubIds);
		return hubClient.getHubs(Map.of("ids", hubIds)).data().hubList();
	}
}
