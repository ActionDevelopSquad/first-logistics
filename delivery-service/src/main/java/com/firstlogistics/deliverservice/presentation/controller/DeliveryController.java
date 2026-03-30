package com.firstlogistics.deliverservice.presentation.controller;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.domain.exception.DeliverySuccessCode;
import com.firstlogistics.deliverservice.presentation.dto.request.DeliveryCreateRequest;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryResponse;
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
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

	private final DeliveryCommandService deliveryCommandService;

	@PostMapping
	public ResponseEntity<ApiResponse<DeliveryResponse>> createDelivery(
		@RequestBody @Valid DeliveryCreateRequest request,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		DeliveryResponse response = DeliveryResponse.from(deliveryCommandService.createDelivery(request.toCommand()));
		return ResponseEntity.status(DeliverySuccessCode.DELIVERY_CREATED.getStatus())
			.body(ApiResponse.success(DeliverySuccessCode.DELIVERY_CREATED, response));
	}
}
