package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.vo.Address;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryStaffId;
import com.firstlogistics.deliverservice.domain.vo.GeoLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
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
	private DeliveryStaffId receiverCompanyDeliveryStaffId;
	private UUID currentHubId;
	private List<DeliveryRoute> routes;

	public static Delivery create(
		UUID orderId,
		UUID sourceHubId,
		UUID destinationHubId,
		String roadAddress,
		String detailAddress,
		double latitude,
		double longitude,
		UUID receiverId,
		String receiverSlackId,
		UUID receiverCompanyId,
		DeliveryStaffId receiverCompanyDeliveryStaffId
	) {
		return new Delivery(
			DeliveryId.generate(), orderId, DeliveryStatus.CREATED,
			sourceHubId, destinationHubId,
			Address.of(roadAddress, detailAddress),
			GeoLocation.of(latitude, longitude),
			receiverId, receiverSlackId, receiverCompanyId, receiverCompanyDeliveryStaffId,
			sourceHubId,
			new ArrayList<>()
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

	public void assignRoute(DeliveryRoute route) {
		this.routes.add(route);
	}
}
