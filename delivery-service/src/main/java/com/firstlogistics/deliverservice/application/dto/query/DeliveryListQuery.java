package com.firstlogistics.deliverservice.application.dto.query;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;

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
	String staffName,
	String staffPhone,

	LocalDateTime startDate,
	LocalDateTime endDate,

	UUID cursorId,
	LocalDateTime cursorCreatedAt,
	int size
) {

	private static final List<Integer> ALLOWED_SIZES = List.of(10, 30, 50);
	private static final int DEFAULT_SIZE = 10;

	public int resolvedSize() {
		return ALLOWED_SIZES.contains(size) ? size : DEFAULT_SIZE;
	}

	public DeliveryListQuery withScope(DeliveryScope scope) {
		return new DeliveryListQuery(
			this.role, this.userId, scope,
			this.orderId, this.status,
			this.sourceHubId, this.destinationHubId,
			this.receiverCompanyId, this.receiverId, this.resolvedReceiverIds, this.receiverName, this.receiverPhone,
			this.staffName, this.staffPhone,
			this.startDate, this.endDate,
			this.cursorId, this.cursorCreatedAt, this.size
		);
	}

	public DeliveryListQuery withResolvedReceiverIds(List<UUID> resolvedReceiverIds) {
		return new DeliveryListQuery(
			this.role, this.userId, this.scope,
			this.orderId, this.status,
			this.sourceHubId, this.destinationHubId,
			this.receiverCompanyId, this.receiverId, resolvedReceiverIds, this.receiverName, this.receiverPhone,
			this.staffName, this.staffPhone,
			this.startDate, this.endDate,
			this.cursorId, this.cursorCreatedAt, this.size
		);
	}
}
