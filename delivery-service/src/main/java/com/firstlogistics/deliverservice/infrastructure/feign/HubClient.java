package com.firstlogistics.deliverservice.infrastructure.feign;

import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.config.FeignErrorDecoder;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.FeignResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubStaffResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hub-service", configuration = FeignErrorDecoder.class)
public interface HubClient {

	@GetMapping("/api/v1/hub-connections/routes")
	FeignResponse<HubRouteResponse> getHubRoute(
		@RequestParam("sourceHubId") UUID sourceHubId,
		@RequestParam("destinationHubId") UUID destinationHubId
	);

	@GetMapping("/api/v1/hub-managers/{managerId}")
	FeignResponse<HubStaffResponse> getHubStaff(@PathVariable("managerId") UUID managerId);

	@PostMapping("/api/v1/hubs/ids")
	FeignResponse<List<HubResponse>> getHubs(@RequestBody List<UUID> hubIds);
}
