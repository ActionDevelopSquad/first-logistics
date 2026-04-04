package com.firstlogistics.deliverservice.presentation;

import com.firstlogistics.deliverservice.application.DeliveryManagerCommandService;
import com.firstlogistics.deliverservice.presentation.dto.request.CreateDeliveryManagerRequest;
import com.firstlogistics.deliverservice.presentation.dto.response.CreateDeliveryManagerResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery-managers")
public class DeliveryManagerController {

	private final DeliveryManagerCommandService deliveryManagerCommandService;

	@PostMapping
	public ResponseEntity<ApiResponse<CreateDeliveryManagerResponse>> createDeliveryManager(
		@RequestBody @Valid CreateDeliveryManagerRequest request,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.status(DeliveryManagerSuccessCode.DELIVERY_MANAGER_CREATED.getStatus())
			.body(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_CREATED,
				CreateDeliveryManagerResponse.from(
					deliveryManagerCommandService.createDeliveryManager(request.toCommand(), role, userId))
			));
	}
}
