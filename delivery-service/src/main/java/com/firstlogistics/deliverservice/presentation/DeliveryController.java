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
import common.security.aop.OnlyMaster;
import common.security.aop.RequireRole;
import common.security.domain.CustomUserDetails;
import common.security.entity.enums.UserRole;
import common.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

	private final DeliveryCommandFacade deliveryCommandFacade;
	private final DeliveryCommandService deliveryCommandService;
	private final DeliveryQueryService deliveryQueryService;

	@OnlyMaster
	@PostMapping
	public ResponseEntity<ApiResponse<CreateDeliveryResponse>> createDelivery(
		@RequestBody @Valid CreateDeliveryRequest request
	) {
		log.info("[배송 생성] 요청 - orderId: {}", request.orderId());
		return ResponseEntity.status(DeliverySuccessCode.DELIVERY_CREATED.getStatus())
			.body(ApiResponse.success(DeliverySuccessCode.DELIVERY_CREATED,
					CreateDeliveryResponse.from(deliveryCommandFacade.createDelivery(request.toCommand()))
			));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER, UserRole.COMPANY_MANAGER})
	@GetMapping
	public ResponseEntity<ApiResponse<DeliveryListResponse>> getDeliveries(
		@ModelAttribute DeliveryListRequest request
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 목록 조회] 요청 - role: {}, userId: {}", user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_LIST_FOUND,
				DeliveryListResponse.from(deliveryQueryService.getDeliveries(request.toQuery(user.getRole().name(), user.getUserId()))))
		);
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER, UserRole.COMPANY_MANAGER})
	@GetMapping("/{deliveryId}")
	public ResponseEntity<ApiResponse<DeliveryDetailResponse>> getDelivery(
		@PathVariable UUID deliveryId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 상세 조회] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_DETAIL_FOUND,
				DeliveryDetailResponse.from(deliveryQueryService.getDelivery(deliveryId, user.getRole(), user.getUserId())))
		);
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER})
	@PatchMapping("/{deliveryId}")
	public ResponseEntity<ApiResponse<UpdateDeliveryResponse>> updateDelivery(
		@PathVariable UUID deliveryId,
		@RequestBody UpdateDeliveryRequest request
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 수정] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_UPDATED,
				UpdateDeliveryResponse.from(deliveryCommandService.updateDelivery(request.toCommand(deliveryId, user.getRole(), user.getUserId())))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER})
	@PostMapping("/{deliveryId}/start")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> startDelivery(
		@PathVariable UUID deliveryId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 허브 출발] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_STARTED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.startHubDelivery(ChangeDeliveryStatusCommand.of(deliveryId), user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.DELIVERY_MANAGER})
	@PostMapping("/{deliveryId}/arrive-hub")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> arriveHub(
		@PathVariable UUID deliveryId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 허브 도착] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_HUB_ARRIVED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.arriveHub(ChangeDeliveryStatusCommand.of(deliveryId), user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER})
	@PostMapping("/{deliveryId}/receive-hub")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> receiveAtHub(
		@PathVariable UUID deliveryId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 허브 입고] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_RECEIVED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.receiveAtHub(ChangeDeliveryStatusCommand.of(deliveryId), user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER})
	@PostMapping("/{deliveryId}/start-company")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> startCompanyDelivery(
		@PathVariable UUID deliveryId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 업체 출발] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_COMPANY_STARTED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.startCompanyDelivery(ChangeDeliveryStatusCommand.of(deliveryId), user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.DELIVERY_MANAGER})
	@PostMapping("/{deliveryId}/complete")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> completeDelivery(
		@PathVariable UUID deliveryId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 완료] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_COMPLETED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.completeDelivery(ChangeDeliveryStatusCommand.of(deliveryId), user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER})
	@PostMapping("/{deliveryId}/cancel")
	public ResponseEntity<ApiResponse<ChangeDeliveryStatusResponse>> cancelDelivery(
		@PathVariable UUID deliveryId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 취소] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_CANCELLED,
				ChangeDeliveryStatusResponse.from(deliveryCommandService.cancelDelivery(ChangeDeliveryStatusCommand.of(deliveryId), user.getRole(), user.getUserId()))
		));
	}

	@RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER})
	@DeleteMapping("/{deliveryId}")
	public ResponseEntity<ApiResponse<Void>> deleteDelivery(
		@PathVariable UUID deliveryId
	) {
		CustomUserDetails user = SecurityUtils.currentUser();
		log.info("[배송 삭제] 요청 - deliveryId: {}, role: {}, userId: {}", deliveryId, user.getRole(), user.getUserId());
		deliveryCommandService.deleteDelivery(deliveryId, user.getRole(), user.getUserId());
		return ResponseEntity.ok(ApiResponse.success(DeliverySuccessCode.DELIVERY_DELETED, null));
	}
}
