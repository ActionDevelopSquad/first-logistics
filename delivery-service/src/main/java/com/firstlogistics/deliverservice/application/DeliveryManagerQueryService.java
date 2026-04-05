package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryManagerListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryManagerDetailResult;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryManagerListResult;
import com.firstlogistics.deliverservice.application.permission.DeliveryPermissionValidator;
import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.projection.DeliveryManagerSummaryProjection;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerQueryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.spec.DeliveryManagerSearchSpec;
import com.firstlogistics.deliverservice.domain.spec.DeliveryScope;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryManagerQueryService {

	private final DeliveryManagerRepository deliveryManagerRepository;
	private final DeliveryManagerQueryRepository deliveryManagerQueryRepository;
	private final DeliveryPermissionValidator deliveryPermissionValidator;
	private final HubManagerPort hubManagerPort;

	private static final Set<UserRole> ALLOWED_ROLES =
		Set.of(UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER);

	public DeliveryManagerListResult getDeliveryManagers(DeliveryManagerListQuery query) {
		UserRole userRole = deliveryPermissionValidator.validateRole(query.role(), ALLOWED_ROLES);

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

	public DeliveryManagerDetailResult getDeliveryManager(UUID managerId, String role, UUID requestUserId) {
		UserRole userRole = deliveryPermissionValidator.validateRole(role, ALLOWED_ROLES);

		DeliveryManager manager = deliveryManagerRepository.findById(DeliveryManagerId.of(managerId))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND));

		UUID hubId =
			userRole == UserRole.HUB_MANAGER
				? hubManagerPort.getHubManager(requestUserId).hubId()
				: null;
		UUID deliveryManagerId =
			userRole == UserRole.DELIVERY_MANAGER
				? deliveryManagerRepository.findByUserId(requestUserId)
					.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND))
					.getId().id()
				: null;

		DeliveryScope scope = DeliveryScope.from(role, hubId, null, deliveryManagerId);
		validateDetailAccess(scope, manager);

		return DeliveryManagerDetailResult.from(manager);
	}

	public DeliveryManagerDetailResult getDeliveryManagerByUserId(UUID targetUserId, String role, UUID requestUserId) {
		deliveryPermissionValidator.validateRole(role, ALLOWED_ROLES);

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
