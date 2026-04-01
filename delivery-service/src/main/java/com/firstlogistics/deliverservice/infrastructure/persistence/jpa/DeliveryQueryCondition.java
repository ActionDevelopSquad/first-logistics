package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeliveryQueryCondition {

	private static final QDeliveryJpaEntity delivery = QDeliveryJpaEntity.deliveryJpaEntity;
	private static final QDeliveryRouteJpaEntity route = QDeliveryRouteJpaEntity.deliveryRouteJpaEntity;

	public static BooleanExpression scopeCondition(DeliveryListQuery query) {
		return switch (query.scope().role()) {
			case HUB_MANAGER -> scopeForHubManager(query.scope().scopeId());
			case DELIVERY_MANAGER -> scopeForDeliveryManager(query.scope().scopeId());
			case COMPANY_MANAGER -> scopeForCompanyManager(query.scope().scopeId());
			default -> null;
		};
	}

	public static BooleanExpression notDeleted() {
		return delivery.deletedAt.isNull();
	}

	public static BooleanExpression scopeForHubManager(UUID scopeHubId) {
		if (scopeHubId == null) return null;
		return delivery.sourceHubId.eq(scopeHubId).or(delivery.destinationHubId.eq(scopeHubId));
	}

	public static BooleanExpression scopeForCompanyManager(UUID scopeCompanyId) {
		if (scopeCompanyId == null) return null;
		return delivery.receiverCompanyId.eq(scopeCompanyId);
	}

	public static BooleanExpression scopeForDeliveryManager(UUID scopeStaffId) {
		if (scopeStaffId == null) return null;
		return route.deliveryStaffId.eq(scopeStaffId)
			.or(delivery.receiverCompanyDeliveryStaffId.eq(scopeStaffId));
	}

	public static BooleanExpression orderIdEq(DeliveryListQuery query) {
		return query.orderId() != null ? delivery.orderId.eq(query.orderId()) : null;
	}

	public static BooleanExpression statusEq(DeliveryListQuery query) {
		return query.status() != null ? delivery.status.eq(query.status()) : null;
	}

	public static BooleanExpression sourceHubEq(DeliveryListQuery query) {
		return query.sourceHubId() != null ? delivery.sourceHubId.eq(query.sourceHubId()) : null;
	}

	public static BooleanExpression destinationHubEq(DeliveryListQuery query) {
		return query.destinationHubId() != null ? delivery.destinationHubId.eq(query.destinationHubId()) : null;
	}

	public static BooleanExpression receiverCompanyIdEq(DeliveryListQuery query) {
		return query.receiverCompanyId() != null ? delivery.receiverCompanyId.eq(query.receiverCompanyId()) : null;
	}

	public static BooleanExpression receiverIdEq(DeliveryListQuery query) {
		return query.receiverId() != null ? delivery.receiverId.eq(query.receiverId()) : null;
	}

	public static BooleanExpression resolvedReceiverIdIn(DeliveryListQuery query) {
		if (query.resolvedReceiverIds() == null || query.resolvedReceiverIds().isEmpty()) return null;
		return delivery.receiverId.in(query.resolvedReceiverIds());
	}

	public static BooleanExpression staffNameContains(
		DeliveryListQuery query,
		QDeliveryStaffJpaEntity routeStaff,
		QDeliveryStaffJpaEntity companyStaff
	) {
		if (query.staffName() == null) return null;
		return routeStaff.staffName.containsIgnoreCase(query.staffName())
			.or(companyStaff.staffName.containsIgnoreCase(query.staffName()));
	}

	public static BooleanExpression staffPhoneContains(
		DeliveryListQuery query,
		QDeliveryStaffJpaEntity routeStaff,
		QDeliveryStaffJpaEntity companyStaff
	) {
		if (query.staffPhone() == null) return null;
		return routeStaff.phoneNumber.containsIgnoreCase(query.staffPhone())
			.or(companyStaff.phoneNumber.containsIgnoreCase(query.staffPhone()));
	}

	public static BooleanExpression dateRange(DeliveryListQuery query) {
		LocalDateTime start = query.startDate();
		LocalDateTime end = query.endDate();
		if (start == null && end == null) return null;
		if (start == null) return delivery.createdAt.loe(end);
		if (end == null) return delivery.createdAt.goe(start);
		return delivery.createdAt.goe(start).and(delivery.createdAt.loe(end));
	}

	public static BooleanExpression cursorCondition(DeliveryListQuery query) {
		if (query.cursorCreatedAt() == null || query.cursorId() == null) return null;
		return delivery.createdAt.lt(query.cursorCreatedAt())
			.or(delivery.createdAt.eq(query.cursorCreatedAt())
				.and(delivery.id.lt(query.cursorId())));
	}
}
