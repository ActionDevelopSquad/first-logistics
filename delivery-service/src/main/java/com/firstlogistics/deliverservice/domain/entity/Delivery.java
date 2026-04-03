package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.vo.Address;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import com.firstlogistics.deliverservice.domain.vo.GeoLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Delivery {

	private DeliveryId id;
	private UUID orderId;
	private DeliveryStatus status;
	private UUID sourceHubId;
	private UUID destinationHubId;
	private Address deliveryAddress;
	private GeoLocation deliveryLocation;
	private UUID receiverId;
	private String receiverSlackId;
	private UUID receiverCompanyId;
	private DeliveryManagerId receiverCompanyDeliveryManagerId;
	private UUID currentHubId;
	private List<DeliveryRoute> routes;

	public static Delivery create(
		UUID orderId,
		UUID sourceHubId,
		UUID destinationHubId,
		String roadAddress,
		String detailAddress,
		UUID receiverId,
		String receiverSlackId,
		UUID receiverCompanyId,
		DeliveryManagerId receiverCompanyDeliveryManagerId
	) {
		if (orderId == null || sourceHubId == null || destinationHubId == null
			|| receiverId == null || receiverCompanyId == null || receiverCompanyDeliveryManagerId == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_PARAMS);
		}
		if (receiverSlackId == null || receiverSlackId.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_PARAMS);
		}
		return new Delivery(
			DeliveryId.generate(), orderId, DeliveryStatus.CREATED,
			sourceHubId, destinationHubId,
			Address.of(roadAddress, detailAddress),
			null,
			receiverId, receiverSlackId, receiverCompanyId, receiverCompanyDeliveryManagerId,
			sourceHubId,
			new ArrayList<>()
		);
	}

	public static Delivery reconstitute(
		DeliveryId id,
		UUID orderId,
		DeliveryStatus status,
		UUID sourceHubId,
		UUID destinationHubId,
		Address deliveryAddress,
		GeoLocation deliveryLocation,
		UUID receiverId,
		String receiverSlackId,
		UUID receiverCompanyId,
		DeliveryManagerId receiverCompanyDeliveryManagerId,
		UUID currentHubId,
		List<DeliveryRoute> routes
	) {
		return new Delivery(
			id, orderId, status,
			sourceHubId, destinationHubId,
			deliveryAddress, deliveryLocation,
			receiverId, receiverSlackId, receiverCompanyId, receiverCompanyDeliveryManagerId,
			currentHubId, new ArrayList<>(routes)
		);
	}

	public void moveToNextHub() {
		this.status = DeliveryStatus.HUB_MOVING;
	}

	public void arriveAtHub() {
		this.status = DeliveryStatus.HUB_WAITING;
	}

	public void arriveAtDestination() {
		this.status = DeliveryStatus.DESTINATION_ARRIVED;
	}

	public void startLastMile() {
		this.status = DeliveryStatus.FOR_COMPANY_MOVING;
	}

	public void completeDelivery() {
		this.status = DeliveryStatus.COMPLETED;
	}

	public void cancelByOrder() {
		this.status = DeliveryStatus.CANCELLED;
	}

	public void reassignReceiver(UUID receiverId, String receiverSlackId) {
		validateBeforeDelivery();
		if (receiverId != null) {
			this.receiverId = receiverId;
		}
		if (receiverSlackId != null && !receiverSlackId.isBlank()) {
			this.receiverSlackId = receiverSlackId;
		}
	}

	public void assignRoute(DeliveryRoute route, DeliveryManagerId managerId) {
		route.assignManager(managerId);
		this.routes.add(route);
	}

	private void validateBeforeDelivery() {
		if (this.status != DeliveryStatus.CREATED) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_MODIFIABLE);
		}
	}
}
