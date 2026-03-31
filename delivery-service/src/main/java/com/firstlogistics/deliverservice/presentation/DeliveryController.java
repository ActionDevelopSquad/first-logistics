package com.firstlogistics.deliverservice.presentation;

import com.firstlogistics.deliverservice.application.facade.DeliveryCreateFacade;
import com.firstlogistics.deliverservice.domain.exception.DeliverySuccessCode;
import com.firstlogistics.deliverservice.presentation.dto.request.DeliveryCreateRequest;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

	private final DeliveryCreateFacade deliveryCreateFacade;

	@PostMapping
	public ResponseEntity<ApiResponse<DeliveryResponse>> createDelivery(
		@RequestBody @Valid DeliveryCreateRequest request,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		DeliveryResponse response = DeliveryResponse.from(deliveryCreateFacade.createDelivery(request.toCommand()));
		return ResponseEntity.status(DeliverySuccessCode.DELIVERY_CREATED.getStatus())
			.body(ApiResponse.success(DeliverySuccessCode.DELIVERY_CREATED, response));
	}
}
