package com.firstlogistics.deliverservice.application.dto.query;

import com.firstlogistics.deliverservice.application.policy.PaginationPolicy;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.spec.DeliveryScope;
import com.firstlogistics.deliverservice.domain.spec.DeliverySearchSpec;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryListQuery(
	String role,
	UUID userId,
	DeliveryScope scope,

	UUID orderId,
	DeliveryStatus status,
	UUID sourceHubId,
	UUID destinationHubId,
	UUID receiverCompanyId,
	UUID receiverId,
	List<UUID> resolvedReceiverIds,
	String receiverName,
	String receiverPhone,
	String managerName,
	String managerPhone,

	LocalDateTime startDate,
	LocalDateTime endDate,

	UUID cursorId,
	LocalDateTime cursorCreatedAt,
	int size
) {
	public DeliveryListQuery {
		if (!UserRole.isValid(role)) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_QUERY_PARAMS);
		}
		if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DATE_RANGE);
		}
		size = PaginationPolicy.resolveSize(size);
	}

	public boolean hasReceiverNameOrPhoneFilter() {
		return hasText(receiverName) || hasText(receiverPhone);
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	public DeliveryListQuery withScope(DeliveryScope scope) {
		return new DeliveryListQuery(
			this.role, this.userId, scope,
			this.orderId, this.status,
			this.sourceHubId, this.destinationHubId,
			this.receiverCompanyId, this.receiverId, this.resolvedReceiverIds, this.receiverName, this.receiverPhone,
			this.managerName, this.managerPhone,
			this.startDate, this.endDate,
			this.cursorId, this.cursorCreatedAt, this.size
		);
	}

	public DeliveryListQuery withResolvedReceiverIds(List<UUID> resolvedReceiverIds) {
		return new DeliveryListQuery(
			this.role, this.userId, this.scope,
			this.orderId, this.status,
			this.sourceHubId, this.destinationHubId,
			this.receiverCompanyId, this.receiverId,
			resolvedReceiverIds != null ? List.copyOf(resolvedReceiverIds) : null,
			this.receiverName, this.receiverPhone,
			this.managerName, this.managerPhone,
			this.startDate, this.endDate,
			this.cursorId, this.cursorCreatedAt, this.size
		);
	}

	public DeliverySearchSpec toSpec() {
		return DeliverySearchSpec.of(
			this.scope,
			this.orderId, this.status,
			this.sourceHubId, this.destinationHubId,
			this.receiverCompanyId, this.receiverId, this.resolvedReceiverIds,
			this.managerName, this.managerPhone,
			this.startDate, this.endDate,
			this.cursorId, this.cursorCreatedAt, this.size
		);
	}
}
