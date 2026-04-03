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
	private DeliveryManagerId deliveryManagerId;

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
		DeliveryManagerId deliveryManagerId
	) {
		return new DeliveryRoute(
			id, deliveryId, deliveryRouteSequence,
			sourceHubId, destinationHubId,
			estimatedDistance, estimatedDuration,
			actualDistance, actualDuration,
			actualDestinationAddress, actualDestinationLocation,
			status, deliveryManagerId
		);
	}

	public void startRoute() {
		if (this.status != RouteStatus.CREATED) {
			throw new DeliveryException(DeliveryErrorCode.ROUTE_ALREADY_STARTED);
		}
		this.status = RouteStatus.MOVING;
	}

	public void arriveAtDestination() {
		if (this.status != RouteStatus.MOVING) {
			throw new DeliveryException(DeliveryErrorCode.ROUTE_NOT_IN_TRANSIT);
		}
		this.status = RouteStatus.ARRIVED;
	}

	public void completeRoute() {
		this.status = RouteStatus.COMPLETED;
	}

	public void assignManager(DeliveryManagerId managerId) {
		this.deliveryManagerId = managerId;
	}
}
