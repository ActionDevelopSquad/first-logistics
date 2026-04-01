package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.vo.*;
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
	private int deliveryRouteSequence;
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
		int deliveryRouteSequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes
	) {
		if (deliveryId == null || sourceHubId == null || destinationHubId == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_ROUTE_PARAMS);
		}
		return new DeliveryRoute(
			DeliveryRouteId.generate(), deliveryId, deliveryRouteSequence,
			sourceHubId, destinationHubId,
			Distance.of(estimatedDistanceMeters),
			Time.of(estimatedDurationMinutes),
			null, null, null, null,
			RouteStatus.CREATED, null
		);
	}

	public static DeliveryRoute reconstitute(
		DeliveryRouteId id,
		DeliveryId deliveryId,
		int deliveryRouteSequence,
		UUID sourceHubId,
		UUID destinationHubId,
		Distance estimatedDistance,
		Time estimatedDuration,
		Distance actualDistance,
		Time actualDuration,
		Address actualDestinationAddress,
		GeoLocation actualDestinationLocation,
		RouteStatus status,
		DeliveryStaffId deliveryStaffId
	) {
		return new DeliveryRoute(
			id, deliveryId, deliveryRouteSequence,
			sourceHubId, destinationHubId,
			estimatedDistance, estimatedDuration,
			actualDistance, actualDuration,
			actualDestinationAddress, actualDestinationLocation,
			status, deliveryStaffId
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
