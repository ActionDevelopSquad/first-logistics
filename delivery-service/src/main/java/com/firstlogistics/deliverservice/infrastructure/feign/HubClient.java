package com.firstlogistics.deliverservice.infrastructure.feign;

import com.firstlogistics.deliverservice.infrastructure.feign.config.FeignErrorDecoder;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.FeignApiResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.HubListFeignResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "hub-service", url = "${hub-service.url:}", configuration = FeignErrorDecoder.class)
public interface HubClient {

	@GetMapping("/api/v1/hub-connections/routes")
	FeignApiResponse<HubRouteResponse> getHubRoute(
		@RequestParam("sourceHubId") UUID sourceHubId,
		@RequestParam("destinationHubId") UUID destinationHubId,
		@RequestParam("destinationCompanyId") UUID receiverCompanyId
	);

	@GetMapping("/api/v1/hub-managers/users/{userId}")
	FeignApiResponse<HubManagerResponse> getHubManagerByUserId(@PathVariable("userId") UUID userId);

	@PostMapping("/api/v1/hubs/ids")
	FeignApiResponse<HubListFeignResponse> getHubs(@RequestBody Map<String, List<UUID>> request);
}
