package com.firstlogistics.deliverservice.presentation;

import com.firstlogistics.deliverservice.application.DeliveryManagerCommandService;
import com.firstlogistics.deliverservice.application.DeliveryManagerQueryService;
import com.firstlogistics.deliverservice.application.dto.query.DeliveryManagerListQuery;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.presentation.dto.request.CreateDeliveryManagerRequest;
import com.firstlogistics.deliverservice.presentation.dto.request.UpdateDeliveryManagerRequest;
import com.firstlogistics.deliverservice.presentation.dto.response.CreateDeliveryManagerResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryManagerDetailResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.DeliveryManagerListResponse;
import com.firstlogistics.deliverservice.presentation.dto.response.UpdateDeliveryManagerResponse;
import common.response.ApiResponse;
import common.security.aop.RequireRole;
import common.security.domain.CustomUserDetails;
import common.security.entity.enums.UserRole;
import common.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER})
	@PostMapping
	public ResponseEntity<ApiResponse<CreateDeliveryManagerResponse>> createDeliveryManager(
		@RequestBody @Valid CreateDeliveryManagerRequest request
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		return ResponseEntity.status(DeliveryManagerSuccessCode.DELIVERY_MANAGER_CREATED.getStatus())
			.body(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_CREATED,
				CreateDeliveryManagerResponse.from(
					deliveryManagerCommandService.createDeliveryManager(request.toCommand(), user.getRole(), user.getUserId()))
			));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER})
	@GetMapping
	public ResponseEntity<ApiResponse<DeliveryManagerListResponse>> getDeliveryManagers(
		@RequestParam(required = false) ManagerType managerType,
		@RequestParam(required = false) UUID hubId,
		@RequestParam(required = false) String managerName,
		@RequestParam(required = false) String phoneNumber,
		@RequestParam(required = false) UUID cursorId,
		@RequestParam(required = false) LocalDateTime cursorCreatedAt,
		@RequestParam(defaultValue = "10") int size
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		DeliveryManagerListQuery query = new DeliveryManagerListQuery(
			user.getRole().name(), user.getUserId(), managerType, hubId, managerName, phoneNumber,
			cursorId, cursorCreatedAt, size
		);
		return ResponseEntity.ok(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_LIST_FOUND,
			DeliveryManagerListResponse.from(deliveryManagerQueryService.getDeliveryManagers(query))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER})
	@GetMapping("/{managerId}")
	public ResponseEntity<ApiResponse<DeliveryManagerDetailResponse>> getDeliveryManager(
		@PathVariable UUID managerId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		return ResponseEntity.ok(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_FOUND,
			DeliveryManagerDetailResponse.from(deliveryManagerQueryService.getDeliveryManager(managerId, user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER})
	@PatchMapping("/{managerId}")
	public ResponseEntity<ApiResponse<UpdateDeliveryManagerResponse>> updateDeliveryManager(
		@PathVariable UUID managerId,
		@RequestBody @Valid UpdateDeliveryManagerRequest request
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		return ResponseEntity.ok(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_UPDATED,
			UpdateDeliveryManagerResponse.from(
				deliveryManagerCommandService.updateDeliveryManager(request.toCommand(managerId), user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER})
	@GetMapping("/users/{targetUserId}")
	public ResponseEntity<ApiResponse<DeliveryManagerDetailResponse>> getDeliveryManagerByUserId(
		@PathVariable UUID targetUserId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		return ResponseEntity.ok(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_FOUND,
			DeliveryManagerDetailResponse.from(deliveryManagerQueryService.getDeliveryManagerByUserId(targetUserId, user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER})
	@DeleteMapping("/{managerId}")
	public ResponseEntity<ApiResponse<Void>> deleteDeliveryManager(
		@PathVariable UUID managerId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		deliveryManagerCommandService.deleteDeliveryManager(managerId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliveryManagerSuccessCode.DELIVERY_MANAGER_DELETED, null));
	}
}
