package com.firstlogistics.deliverservice.presentation;

import com.firstlogistics.deliverservice.application.DeliveryManagerCommandService;
import com.firstlogistics.deliverservice.application.DeliveryManagerQueryService;
import com.firstlogistics.deliverservice.application.dto.query.DeliveryManagerListQuery;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.presentation.dto.request.CreateDeliveryManagerRequest;
import com.firstlogistics.deliverservice.presentation.dto.response.CreateDeliveryManagerResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryManagerDetailResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryManagerListResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery-managers")
public class DeliveryManagerController {

	private final DeliveryManagerCommandService deliveryManagerCommandService;
	private final DeliveryManagerQueryService deliveryManagerQueryService;

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

	@GetMapping
	public ResponseEntity<ApiResponse<DeliveryManagerListResponse>> getDeliveryManagers(
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role,
		@RequestParam(required = false) ManagerType managerType,
		@RequestParam(required = false) UUID hubId,
		@RequestParam(required = false) String managerName,
		@RequestParam(required = false) String phoneNumber,
		@RequestParam(required = false) UUID cursorId,
		@RequestParam(required = false) LocalDateTime cursorCreatedAt,
		@RequestParam(defaultValue = "10") int size
	) {
		DeliveryManagerListQuery query = new DeliveryManagerListQuery(
			role, userId, managerType, hubId, managerName, phoneNumber,
			cursorId, cursorCreatedAt, size
		);
		return ResponseEntity.ok(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_LIST_FOUND,
			DeliveryManagerListResponse.from(deliveryManagerQueryService.getDeliveryManagers(query))
		));
	}

	@GetMapping("/{managerId}")
	public ResponseEntity<ApiResponse<DeliveryManagerDetailResponse>> getDeliveryManager(
		@PathVariable UUID managerId,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_FOUND,
			DeliveryManagerDetailResponse.from(deliveryManagerQueryService.getDeliveryManager(managerId, role, userId))
		));
	}

	@GetMapping("/users/{targetUserId}")
	public ResponseEntity<ApiResponse<DeliveryManagerDetailResponse>> getDeliveryManagerByUserId(
		@PathVariable UUID targetUserId,
		@RequestHeader("X-User-Id") UUID userId,
		@RequestHeader("X-User-Role") String role
	) {
		return ResponseEntity.ok(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_FOUND,
			DeliveryManagerDetailResponse.from(deliveryManagerQueryService.getDeliveryManagerByUserId(targetUserId, role, userId))
		));
	}
}
