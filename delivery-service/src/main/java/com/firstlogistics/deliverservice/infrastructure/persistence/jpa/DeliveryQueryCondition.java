package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.spec.DeliverySearchSpec;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeliveryQueryCondition {

	private static final QDeliveryJpaEntity delivery = QDeliveryJpaEntity.deliveryJpaEntity;
	private static final QDeliveryRouteJpaEntity route = QDeliveryRouteJpaEntity.deliveryRouteJpaEntity;

	public static BooleanExpression scopeCondition(DeliverySearchSpec spec) {
		return switch (spec.scope().role()) {
			case HUB_MANAGER -> scopeForHubManager(spec.scope().scopeId());
			case DELIVERY_MANAGER -> scopeForDeliveryManager(spec.scope().scopeId());
			case COMPANY_MANAGER -> scopeForCompanyManager(spec.scope().scopeId());
			default -> null;
		};
	}

	public static BooleanExpression notDeleted() {
		return delivery.deletedAt.isNull();
	}

	public static BooleanExpression routeNotDeleted() {
		return route.deletedAt.isNull();
	}

	public static BooleanExpression scopeForHubManager(UUID scopeHubId) {
		if (scopeHubId == null) return null;
		return delivery.sourceHubId.eq(scopeHubId).or(delivery.destinationHubId.eq(scopeHubId));
	}

	public static BooleanExpression scopeForCompanyManager(UUID scopeCompanyId) {
		if (scopeCompanyId == null) return null;
		return delivery.receiverCompanyId.eq(scopeCompanyId);
	}

	public static BooleanExpression scopeForDeliveryManager(UUID scopeManagerId) {
		if (scopeManagerId == null) return null;
		return route.deliveryManagerId.eq(scopeManagerId)
			.or(delivery.receiverCompanyDeliveryManagerId.eq(scopeManagerId));
	}

	public static BooleanExpression orderIdEq(DeliverySearchSpec spec) {
		return spec.orderId() != null ? delivery.orderId.eq(spec.orderId()) : null;
	}

	public static BooleanExpression statusEq(DeliverySearchSpec spec) {
		return spec.status() != null ? delivery.status.eq(spec.status()) : null;
	}

	public static BooleanExpression sourceHubEq(DeliverySearchSpec spec) {
		return spec.sourceHubId() != null ? delivery.sourceHubId.eq(spec.sourceHubId()) : null;
	}

	public static BooleanExpression destinationHubEq(DeliverySearchSpec spec) {
		return spec.destinationHubId() != null ? delivery.destinationHubId.eq(spec.destinationHubId()) : null;
	}

	public static BooleanExpression receiverCompanyIdEq(DeliverySearchSpec spec) {
		return spec.receiverCompanyId() != null ? delivery.receiverCompanyId.eq(spec.receiverCompanyId()) : null;
	}

	public static BooleanExpression receiverIdEq(DeliverySearchSpec spec) {
		return spec.receiverId() != null ? delivery.receiverId.eq(spec.receiverId()) : null;
	}

	public static BooleanExpression resolvedReceiverIdIn(DeliverySearchSpec spec) {
		if (spec.resolvedReceiverIds() == null || spec.resolvedReceiverIds().isEmpty()) return null;
		return delivery.receiverId.in(spec.resolvedReceiverIds());
	}

	public static BooleanExpression managerNameContains(
		DeliverySearchSpec spec,
		QDeliveryManagerJpaEntity hubDeliveryManager,
		QDeliveryManagerJpaEntity companyDeliveryManager
	) {
		if (spec.managerName() == null) return null;
		return hubDeliveryManager.managerName.containsIgnoreCase(spec.managerName())
			.or(companyDeliveryManager.managerName.containsIgnoreCase(spec.managerName()));
	}

	public static BooleanExpression managerPhoneContains(
		DeliverySearchSpec spec,
		QDeliveryManagerJpaEntity hubDeliveryManager,
		QDeliveryManagerJpaEntity companyDeliveryManager
	) {
		if (spec.managerPhone() == null) return null;
		return hubDeliveryManager.phoneNumber.containsIgnoreCase(spec.managerPhone())
			.or(companyDeliveryManager.phoneNumber.containsIgnoreCase(spec.managerPhone()));
	}

	public static BooleanExpression dateRange(DeliverySearchSpec spec) {
		LocalDateTime start = spec.startDate();
		LocalDateTime end = spec.endDate();
		if (start == null && end == null) return null;
		if (start == null) return delivery.createdAt.loe(end);
		if (end == null) return delivery.createdAt.goe(start);
		return delivery.createdAt.goe(start).and(delivery.createdAt.loe(end));
	}

	public static BooleanExpression cursorCondition(DeliverySearchSpec spec) {
		if (spec.cursorCreatedAt() == null || spec.cursorId() == null) return null;
		return delivery.createdAt.lt(spec.cursorCreatedAt())
			.or(delivery.createdAt.eq(spec.cursorCreatedAt())
				.and(delivery.id.lt(spec.cursorId())));
	}
}
