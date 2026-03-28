package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
import com.firstlogistics.deliverservice.domain.vo.Distance;
import com.firstlogistics.deliverservice.domain.vo.Time;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

	public static DeliveryRoute create(
		UUID deliveryId,
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes
	) {
		RouteStatus createdStatus = RouteStatus.CREATED;
		return new DeliveryRoute(
				null, deliveryId, sequence,
				sourceHubId, destinationHubId,
				Distance.of(estimatedDistanceMeters),
				Time.of(estimatedDurationMinutes),
				null, null,
				createdStatus, null
		);
	}

	public void departRoute() {
		this.status = RouteStatus.HUB_MOVING;
	}

	public void waitAtHub() {
		this.status = RouteStatus.HUB_WAITING;
	}

	public void assignStaff(UUID staffId) {
		this.deliveryStaffId = staffId;
	}
}
