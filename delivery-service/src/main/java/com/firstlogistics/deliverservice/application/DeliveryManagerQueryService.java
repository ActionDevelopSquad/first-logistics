package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryManagerListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryManagerDetailResult;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryManagerListResult;
import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.projection.DeliveryManagerSummaryProjection;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerQueryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.spec.DeliveryManagerSearchSpec;
import com.firstlogistics.deliverservice.domain.spec.DeliveryScope;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryManagerQueryService {

	private final DeliveryManagerRepository deliveryManagerRepository;
	private final DeliveryManagerQueryRepository deliveryManagerQueryRepository;
	private final HubManagerPort hubManagerPort;

	public DeliveryManagerListResult getDeliveryManagers(DeliveryManagerListQuery query) {
		log.info("[배송담당자 목록 조회] 시작 - role: {}, userId: {}", query.role(), query.userId());
		UserRole userRole = UserRole.valueOf(query.role());

		UUID hubId =
			userRole == UserRole.HUB_MANAGER
				? hubManagerPort.getHubManager(query.userId()).hubId()
				: null;
		UUID deliveryManagerId =
			userRole == UserRole.DELIVERY_MANAGER
				? deliveryManagerRepository.findByUserId(query.userId())
					.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND))
					.getId().id()
				: null;

		DeliveryScope scope = DeliveryScope.from(query.role(), hubId, null, deliveryManagerId);
		DeliveryManagerSearchSpec spec = query.toSpec(scope);

		List<DeliveryManagerSummaryProjection> projections =
			deliveryManagerQueryRepository.findDeliveryManagers(spec);

		boolean hasNext = projections.size() > spec.size();
		List<DeliveryManagerSummaryProjection> content = hasNext
			? projections.subList(0, spec.size())
			: projections;

		return DeliveryManagerListResult.from(content, hasNext);
	}

	public DeliveryManagerDetailResult getDeliveryManager(UUID managerId, UserRole role, UUID requestUserId) {
		log.info("[배송담당자 상세 조회] 시작 - managerId: {}", managerId);
		DeliveryManager manager = deliveryManagerRepository.findById(DeliveryManagerId.of(managerId))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND));

		UUID hubId =
			role == UserRole.HUB_MANAGER
				? hubManagerPort.getHubManager(requestUserId).hubId()
				: null;
		UUID deliveryManagerId =
			role == UserRole.DELIVERY_MANAGER
				? deliveryManagerRepository.findByUserId(requestUserId)
					.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND))
					.getId().id()
				: null;

		DeliveryScope scope = DeliveryScope.from(role.name(), hubId, null, deliveryManagerId);
		validateDetailAccess(scope, manager);

		return DeliveryManagerDetailResult.from(manager);
	}

	public DeliveryManagerDetailResult getDeliveryManagerByUserId(UUID targetUserId, UserRole role, UUID requestUserId) {
		log.info("[배송담당자 userId 조회] 시작 - targetUserId: {}", targetUserId);
		DeliveryManager manager = deliveryManagerRepository.findByUserId(targetUserId)
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND));

		return DeliveryManagerDetailResult.from(manager);
	}

	private void validateDetailAccess(DeliveryScope scope, DeliveryManager manager) {
		switch (scope.role()) {
			case HUB_MANAGER -> {
				if (!scope.scopeId().equals(manager.getHubId())) {
					throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
				}
			}
			case DELIVERY_MANAGER -> {
				if (!scope.scopeId().equals(manager.getId().id())) {
					throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
				}
			}
			default -> { }
		}
	}
}
