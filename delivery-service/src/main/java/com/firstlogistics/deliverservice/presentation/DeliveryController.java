package com.firstlogistics.deliverservice.presentation;

import com.firstlogistics.deliverservice.application.DeliveryQueryService;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryCreateResult;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryDetailResult;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.application.facade.DeliveryCreateFacade;
import com.firstlogistics.deliverservice.presentation.dto.request.DeliveryCreateRequest;
import com.firstlogistics.deliverservice.presentation.dto.request.DeliveryListRequest;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryDetailResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryListResponse;
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
	private final DeliveryQueryService deliveryQueryService;

	@PostMapping
	public ResponseEntity<ApiResponse<DeliveryResponse>> createDelivery(
		@RequestBody @Valid DeliveryCreateRequest request,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		DeliveryCreateResult result = deliveryCreateFacade.createDelivery(request.toCommand());
		DeliveryResponse response = DeliveryResponse.from(result);
		return ResponseEntity.status(DeliverySuccessCode.DELIVERY_CREATED.getStatus())
			.body(ApiResponse.success(DeliverySuccessCode.DELIVERY_CREATED, response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<DeliveryListResponse>> getDeliveries(
		@ModelAttribute DeliveryListRequest request,
		@RequestHeader("X-User-Role") String role,
		@RequestHeader("X-User-Id") UUID userId
	) {
		DeliveryListResult result = deliveryQueryService.getDeliveries(request.toQuery(role, userId));
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_LIST_FOUND, DeliveryListResponse.from(result)));
	}

	@GetMapping("/{deliveryId}")
	public ResponseEntity<ApiResponse<DeliveryDetailResponse>> getDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader("X-User-Role") String role,
		@RequestHeader("X-User-Id") UUID userId
	) {
		DeliveryDetailResult result = deliveryQueryService.getDelivery(deliveryId, role, userId);
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_DETAIL_FOUND, DeliveryDetailResponse.from(result)));
	}
}
