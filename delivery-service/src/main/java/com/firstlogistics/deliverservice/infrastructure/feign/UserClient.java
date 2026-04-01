package com.firstlogistics.deliverservice.infrastructure.feign;

import com.firstlogistics.deliverservice.infrastructure.feign.config.FeignErrorDecoder;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.FeignResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", configuration = FeignErrorDecoder.class)
public interface UserClient {

	@GetMapping("/api/v1/users/{userId}")
	FeignResponse<UserResponse> getUser(@PathVariable("userId") UUID userId);

	@GetMapping("/api/v1/users")
	FeignResponse<List<UserResponse>> findByNameOrPhone(
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "phone", required = false) String phone
	);
}
