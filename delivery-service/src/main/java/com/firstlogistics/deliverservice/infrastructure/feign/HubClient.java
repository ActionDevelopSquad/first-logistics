package com.firstlogistics.deliverservice.infrastructure.feign;

import com.firstlogistics.deliverservice.infrastructure.feign.dto.FeignResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.HubRouteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "hub-service", configuration = com.firstlogistics.deliverservice.infrastructure.feign.config.FeignErrorDecoder.class)
public interface HubClient {

	@GetMapping("/api/v1/hub-connections/routes")
	FeignResponse<HubRouteResponse> getHubRoute(
		@RequestParam UUID sourceHubId,
		@RequestParam UUID destinationHubId
	);
}
