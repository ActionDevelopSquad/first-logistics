package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryDetailResult;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;
import com.firstlogistics.deliverservice.application.permission.DeliveryPermissionValidator;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.projection.DeliveryDetailProjection;
import com.firstlogistics.deliverservice.domain.projection.DeliverySummaryProjection;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryQueryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.spec.DeliveryScope;
import com.firstlogistics.deliverservice.domain.spec.DeliverySearchSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryQueryService {

	private final DeliveryRepository deliveryRepository;
	private final DeliveryManagerRepository deliveryManagerRepository;
	private final DeliveryQueryRepository deliveryQueryRepository;
	private final DeliveryPermissionValidator deliveryPermissionValidator;
	private final UserPort userPort;
	private final HubPort hubPort;
	private final HubManagerPort hubManagerPort;
	private final CompanyPort companyPort;

	public boolean existsByOrderId(UUID orderId) {
		return deliveryRepository.existsByOrderId(orderId);
	}

	public DeliveryListResult getDeliveries(DeliveryListQuery query) {
		log.info("[배송 목록 조회] 시작 - role: {}, userId: {}", query.role(), query.userId());
		UserRole userRole = UserRole.valueOf(query.role());

		UUID hubId =
				userRole == UserRole.HUB_MANAGER
						? hubManagerPort.getHubManager(query.userId()).hubId()
						: null;
		UUID companyId =
				userRole == UserRole.COMPANY_MANAGER
						? companyPort.getCompanyManager(query.userId()).companyId()
						: null;
		UUID deliveryManagerId =
				userRole == UserRole.DELIVERY_MANAGER
						? deliveryManagerRepository.findByUserId(query.userId())
								.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND))
								.getId().id()
						: null;
		DeliveryListQuery resolvedQuery = query.withScope(DeliveryScope.from(query.role(), hubId, companyId, deliveryManagerId));

		if (query.hasReceiverNameOrPhoneFilter()) {
			List<UUID> receiverIds = userPort
					.findByNameOrPhone(query.receiverName(), query.receiverPhone())
					.stream().map(UserResponse::userId).toList();
			resolvedQuery = resolvedQuery.withResolvedReceiverIds(receiverIds);
		}

		DeliverySearchSpec spec = resolvedQuery.toSpec();
		List<DeliverySummaryProjection> results = deliveryQueryRepository.findDeliveries(spec);

		boolean hasNext = results.size() > spec.size();
		if (hasNext) {
			results = results.subList(0, spec.size());
		}
		return DeliveryListResult.from(results, hasNext);
	}

	public DeliveryDetailResult getDelivery(UUID deliveryId, UserRole role, UUID userId) {
		log.info("[배송 상세 조회] 시작 - deliveryId: {}", deliveryId);
		DeliveryDetailProjection deliveryDetail = deliveryQueryRepository.findById(deliveryId)
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		List<DeliveryDetailProjection.RouteDetail> routes = deliveryQueryRepository.findRoutesByDeliveryId(deliveryId);

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(deliveryDetail, routes);
		deliveryPermissionValidator.validate(accessContext, role, userId);

		List<UUID> hubIds = routes.stream()
			.flatMap(route -> Stream.of(route.sourceHubId(), route.destinationHubId()))
			.distinct().toList();

		Map<UUID, HubResponse> hubMap = hubPort.getHubs(hubIds).stream()
			.collect(Collectors.toMap(HubResponse::hubId, hub -> hub));
		if (!hubMap.keySet().containsAll(hubIds)) {
			throw new DeliveryException(DeliveryErrorCode.HUB_NOT_FOUND);
		}
		UserResponse receiver = userPort.getUser(deliveryDetail.receiverId());
		CompanyResponse company = companyPort.getCompany(deliveryDetail.receiverCompanyId());

		return DeliveryDetailResult.from(deliveryDetail, routes, hubMap, receiver, company);
	}

}
