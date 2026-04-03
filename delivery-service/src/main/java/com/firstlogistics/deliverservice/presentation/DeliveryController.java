package com.firstlogistics.deliverservice.presentation;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.DeliveryQueryService;
import com.firstlogistics.deliverservice.application.facade.DeliveryCommandFacade;
import com.firstlogistics.deliverservice.presentation.dto.request.CreateDeliveryRequest;
import com.firstlogistics.deliverservice.presentation.dto.request.DeliveryListRequest;
import com.firstlogistics.deliverservice.presentation.dto.request.UpdateDeliveryRequest;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryDetailResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryListResponse;
import com.firstlogistics.deliverservice.application.dto.command.ChangeDeliveryStatusCommand;
import com.firstlogistics.deliverservice.presentation.dto.response.ChangeDeliveryStatusResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.CreateDeliveryResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.UpdateDeliveryResponse;
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

	private final DeliveryCommandFacade deliveryCommandFacade;
	private final DeliveryCommandService deliveryCommandService;
	private final DeliveryQueryService deliveryQueryService;

	@PostMapping
	public ResponseEntity<ApiResponse<CreateDeliveryResponse>> createDelivery(
		@RequestBody @Valid CreateDeliveryRequest request,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.status(DeliverySuccessCode.DELIVERY_CREATED.getStatus())
			.body(ApiResponse.success(DeliverySuccessCode.DELIVERY_CREATED,
					CreateDeliveryResponse.from(deliveryCommandFacade.createDelivery(request.toCommand()))
			));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<DeliveryListResponse>> getDeliveries(
		@ModelAttribute DeliveryListRequest request,
		@RequestHeader("X-User-Role") String role,
		@RequestHeader("X-User-Id") UUID userId
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_LIST_FOUND,
				DeliveryListResponse.from(deliveryQueryService.getDeliveries(request.toQuery(role, userId))))
		);
	}

	@PatchMapping("/{deliveryId}")
	public ResponseEntity<ApiResponse<UpdateDeliveryResponse>> updateDelivery(
		@PathVariable UUID deliveryId,
		@RequestBody UpdateDeliveryRequest request,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_UPDATED,
				UpdateDeliveryResponse.from(deliveryCommandService.updateDelivery(request.toCommand(deliveryId, role, userId)))
		));
	}

	@PostMapping("/{deliveryId}/start")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> startDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_STARTED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.startHubDelivery(ChangeDeliveryStatusCommand.of(deliveryId, role, userId)))
		));
	}

	@PostMapping("/{deliveryId}/arrive-hub")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> arriveHub(
		@PathVariable UUID deliveryId,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_HUB_ARRIVED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.arriveHub(ChangeDeliveryStatusCommand.of(deliveryId, role, userId)))
		));
	}

	@PostMapping("/{deliveryId}/receive-hub")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> receiveAtHub(
		@PathVariable UUID deliveryId,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_RECEIVED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.receiveAtHub(ChangeDeliveryStatusCommand.of(deliveryId, role, userId)))
		));
	}

	@PostMapping("/{deliveryId}/start-company")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> startCompanyDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_COMPANY_STARTED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.startCompanyDelivery(ChangeDeliveryStatusCommand.of(deliveryId, role, userId)))
		));
	}

	@PostMapping("/{deliveryId}/complete")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> completeDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_COMPLETED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.completeDelivery(ChangeDeliveryStatusCommand.of(deliveryId, role, userId)))
		));
	}

	@PostMapping("/{deliveryId}/cancel")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> cancelDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_CANCELLED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.cancelDelivery(ChangeDeliveryStatusCommand.of(deliveryId, role, userId)))
		));
	}

	@GetMapping("/{deliveryId}")
	public ResponseEntity<ApiResponse<DeliveryDetailResponse>> getDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader("X-User-Role") String role,
		@RequestHeader("X-User-Id") UUID userId
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_DETAIL_FOUND,
				DeliveryDetailResponse.from(deliveryQueryService.getDelivery(deliveryId, role, userId)))
		);
	}
}
