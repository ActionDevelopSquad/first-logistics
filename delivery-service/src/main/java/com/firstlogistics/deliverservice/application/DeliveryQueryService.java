package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.query.DeliveryScope;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.application.enums.UserRole;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.DeliveryQueryRepositoryPort;
import com.firstlogistics.deliverservice.application.port.HubStaffPort;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryQueryService {

	private final DeliveryRepository deliveryRepository;
	private final DeliveryQueryRepositoryPort deliveryQueryRepositoryPort;
	private final UserPort userPort;
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
}
