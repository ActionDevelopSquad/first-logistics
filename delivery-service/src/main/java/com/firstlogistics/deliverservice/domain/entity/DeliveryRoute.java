package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
import com.firstlogistics.deliverservice.domain.vo.Distance;
import com.firstlogistics.deliverservice.domain.vo.Time;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryRoute {

	private UUID id;
	private UUID deliveryId;
	private int sequence;
	private UUID sourceHubId;
	private UUID destinationHubId;
	private Distance estimatedDistance;
	private Time estimatedDuration;
	private Distance actualDistance;
	private Time actualDuration;
	private RouteStatus status;
	private UUID deliveryStaffId;

	private DeliveryRoute(
		UUID deliveryId,
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		Distance estimatedDistance,
		Time estimatedDuration,
		RouteStatus status
	) {
		this.deliveryId = deliveryId;
		this.sequence = sequence;
		this.sourceHubId = sourceHubId;
		this.destinationHubId = destinationHubId;
		this.estimatedDistance = estimatedDistance;
		this.estimatedDuration = estimatedDuration;
		this.status = status;
	}

	public static DeliveryRoute create(
		UUID deliveryId,
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		Distance estimatedDistance,
		Time estimatedDuration
	) {
		RouteStatus createdStatus = RouteStatus.CREATED;
		return new DeliveryRoute(deliveryId, sequence, sourceHubId, destinationHubId, estimatedDistance, estimatedDuration, createdStatus);
	}

	public void departRoute() {
		this.status = RouteStatus.HUB_MOVING;
	}

	public void waitAtHub() {
		this.status = RouteStatus.HUB_WAITING;
	}

	public void arriveRoute(Distance actualDistance, Time actualDuration) {
		this.actualDistance = actualDistance;
		this.actualDuration = actualDuration;
		this.status = RouteStatus.DESTINATION_ARRIVED;
	}

	public void assignStaff(UUID staffId) {
		this.deliveryStaffId = staffId;
	}
}
