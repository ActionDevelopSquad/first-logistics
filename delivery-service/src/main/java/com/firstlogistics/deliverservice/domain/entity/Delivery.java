package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.vo.Address;
import com.firstlogistics.deliverservice.domain.vo.GeoLocation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

	private UUID id;
	private UUID orderId;
	private DeliveryStatus status;
	private UUID sourceHubId;
	private UUID destinationHubId;
	private Address address;
	private GeoLocation geoLocation;
	private UUID receiverId;
	private String receiverSlackId;
	private UUID receiverCompanyDeliveryStaffId;

	private List<DeliveryRoute> routes = new ArrayList<>();

	private Delivery(
		UUID orderId,
		DeliveryStatus status,
		UUID sourceHubId,
		UUID destinationHubId,
		Address address,
		GeoLocation geoLocation,
		UUID receiverId,
		String receiverSlackId,
		UUID receiverCompanyDeliveryStaffId
	) {
		this.orderId = orderId;
		this.status = status;
		this.sourceHubId = sourceHubId;
		this.destinationHubId = destinationHubId;
		this.address = address;
		this.geoLocation = geoLocation;
		this.receiverId = receiverId;
		this.receiverSlackId = receiverSlackId;
		this.receiverCompanyDeliveryStaffId = receiverCompanyDeliveryStaffId;
	}

	public static Delivery create(
		UUID orderId,
		UUID sourceHubId,
		UUID destinationHubId,
		Address address,
		GeoLocation geoLocation,
		UUID receiverId,
		String receiverSlackId,
		UUID receiverCompanyDeliveryStaffId
	) {
		DeliveryStatus createdStatus = DeliveryStatus.CREATED;
		return new Delivery(orderId, createdStatus, sourceHubId, destinationHubId, address, geoLocation, receiverId, receiverSlackId, receiverCompanyDeliveryStaffId);
	}

	public void startDelivery() {
		this.status = DeliveryStatus.HUB_WAITING;
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
		this.status = DeliveryStatus.FOR_VENDOR_MOVING;
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
