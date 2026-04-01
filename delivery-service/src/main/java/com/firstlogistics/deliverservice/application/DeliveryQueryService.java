package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.query.DeliveryScope;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryDetail;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryDetailResult;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.application.enums.UserRole;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.DeliveryQueryRepositoryPort;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.HubStaffPort;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryQueryService {

	private final DeliveryRepository deliveryRepository;
	private final DeliveryQueryRepositoryPort deliveryQueryRepositoryPort;
	private final UserPort userPort;
	private final HubPort hubPort;
	private final HubStaffPort hubStaffPort;
	private final CompanyPort companyPort;

	public boolean existsByOrderId(UUID orderId) {
		return deliveryRepository.existsByOrderId(orderId);
	}

	public DeliveryListResult getDeliveries(DeliveryListQuery query) {
		UserRole userRole = UserRole.valueOf(query.role());
		UUID hubId = userRole == UserRole.HUB_MANAGER
			? hubStaffPort.getHubStaff(query.userId()).hubId()
			: null;
		UUID companyId = userRole == UserRole.COMPANY_MANAGER
			? companyPort.getCompanyByManagerId(query.userId()).companyId()
			: null;
		DeliveryListQuery resolvedQuery = query.withScope(DeliveryScope.from(query.role(), query.userId(), hubId, companyId));

		if (query.hasReceiverSearchCondition()) {
			List<UUID> receiverIds = userPort.findByNameOrPhone(query.receiverName(), query.receiverPhone())
				.stream().map(UserResponse::userId).toList();
			resolvedQuery = resolvedQuery.withResolvedReceiverIds(receiverIds);
		}

		List<DeliveryListResult.DeliverySummary> results = deliveryQueryRepositoryPort.findDeliveries(resolvedQuery);
		boolean hasNext = results.size() > resolvedQuery.size();
		if (hasNext) {
			results = results.subList(0, resolvedQuery.size());
		}
		return DeliveryListResult.from(results, hasNext);
	}

	public DeliveryDetailResult getDelivery(UUID deliveryId, String role, UUID userId) {
		DeliveryDetail deliveryDetail = deliveryQueryRepositoryPort.findById(deliveryId)
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		List<DeliveryDetail.RouteDetail> routes = deliveryQueryRepositoryPort.findRoutesByDeliveryId(deliveryId);

		validateDeliveryAccess(deliveryDetail, routes, role, userId);
		List<UUID> hubIds = routes.stream()
			.flatMap(route -> Stream.of(route.sourceHubId(), route.destinationHubId()))
			.distinct().toList();

		Map<UUID, HubResponse> hubMap = hubPort.getHubs(hubIds).stream()
			.collect(Collectors.toMap(HubResponse::hubId, h -> h));
		UserResponse receiver = userPort.getUser(deliveryDetail.receiverId());
		CompanyResponse company = companyPort.getCompany(deliveryDetail.receiverCompanyId());

		return DeliveryDetailResult.from(deliveryDetail, routes, hubMap, receiver, company);
	}

	private void validateDeliveryAccess(DeliveryDetail deliveryDetail, List<DeliveryDetail.RouteDetail> routes, String role, UUID userId) {
		UserRole userRole = UserRole.valueOf(role);
		switch (userRole) {
			case MASTER -> {}
			case HUB_MANAGER -> {
				UUID hubId = hubStaffPort.getHubStaff(userId).hubId();
				if (!hubId.equals(deliveryDetail.sourceHubId())) {
					throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
				}
			}
			case DELIVERY_MANAGER -> {
				boolean isAssigned = routes.stream()
					.anyMatch(route -> userId.equals(route.deliveryStaffId()));
				if (!isAssigned) {
					throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
				}
			}
			case COMPANY_MANAGER -> {
				UUID companyId = companyPort.getCompanyByManagerId(userId).companyId();
				if (!companyId.equals(deliveryDetail.receiverCompanyId())) {
					throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
				}
			}
		}
	}
}
