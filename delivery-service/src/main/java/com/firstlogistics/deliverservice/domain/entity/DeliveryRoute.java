package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
import com.firstlogistics.deliverservice.domain.vo.Address;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryRouteId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryStaffId;
import com.firstlogistics.deliverservice.domain.vo.Distance;
import com.firstlogistics.deliverservice.domain.vo.GeoLocation;
import com.firstlogistics.deliverservice.domain.vo.Time;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryRoute {

	private DeliveryRouteId id;
	private DeliveryId deliveryId;
	private int sequence;
	private UUID sourceHubId;
	private UUID destinationHubId;
	private Distance estimatedDistance;
	private Time estimatedDuration;
	private Distance actualDistance;
	private Time actualDuration;
	private Address actualDestinationAddress;
	private GeoLocation actualDestinationLocation;
	private RouteStatus status;
	private DeliveryStaffId deliveryStaffId;

	public static DeliveryRoute create(
		DeliveryId deliveryId,
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes
	) {
		return new DeliveryRoute(
			DeliveryRouteId.generate(), deliveryId, sequence,
			sourceHubId, destinationHubId,
			Distance.of(estimatedDistanceMeters),
			Time.of(estimatedDurationMinutes),
			null, null, null, null,
			RouteStatus.CREATED, null
		);
	}

	public void departRoute() {
		this.status = RouteStatus.HUB_MOVING;
	}

	public void waitAtHub() {
		this.status = RouteStatus.HUB_WAITING;
	}

	public void assignStaff(DeliveryStaffId staffId) {
		this.deliveryStaffId = staffId;
	}
}
