package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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

	public void startHubDelivery() {
		validateStatusTransition(Set.of(DeliveryStatus.CREATED, DeliveryStatus.HUB_WAITING));
		if (this.currentHubId.equals(this.destinationHubId)) {
			throw new DeliveryException(DeliveryErrorCode.NOT_HUB_DELIVERY_PHASE);
		}
		DeliveryRoute nextRoute = findNextCreatedRoute();
		nextRoute.startRoute();
		this.status = DeliveryStatus.FOR_HUB_MOVING;
	}

	public void startCompanyDelivery() {
		validateStatusTransition(Set.of(DeliveryStatus.HUB_WAITING));
		if (!this.currentHubId.equals(this.destinationHubId)) {
			throw new DeliveryException(DeliveryErrorCode.NOT_COMPANY_DELIVERY_PHASE);
		}
		DeliveryRoute companyRoute = findNextCreatedRoute();
		companyRoute.startRoute();
		this.status = DeliveryStatus.FOR_COMPANY_MOVING;
	}

	private DeliveryRoute findNextCreatedRoute() {
		return this.routes.stream()
			.filter(route -> route.getStatus() == RouteStatus.CREATED)
			.min(Comparator.comparingInt(DeliveryRoute::getDeliveryRouteSequence))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.NEXT_ROUTE_NOT_FOUND));
	}

	public void arriveAtHub() {
		validateStatusTransition(Set.of(DeliveryStatus.FOR_HUB_MOVING));
		DeliveryRoute currentRoute = this.routes.stream()
			.filter(route -> route.getStatus() == RouteStatus.MOVING)
			.findFirst()
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.MOVING_ROUTE_NOT_FOUND));
		currentRoute.arriveAtDestination();
		this.currentHubId = currentRoute.getDestinationHubId();
		this.status = DeliveryStatus.HUB_ARRIVED;
	}

	public void receiveAtHub() {
		validateStatusTransition(Set.of(DeliveryStatus.HUB_ARRIVED));
		this.status = DeliveryStatus.HUB_WAITING;
	}

	public void completeDelivery() {
		validateStatusTransition(Set.of(DeliveryStatus.FOR_COMPANY_MOVING));
		DeliveryRoute lastRoute = this.routes.stream()
			.max(Comparator.comparingInt(DeliveryRoute::getDeliveryRouteSequence))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.NEXT_ROUTE_NOT_FOUND));
		lastRoute.completeRoute();
		this.status = DeliveryStatus.COMPLETED;
	}

	public void cancelDelivery() {
		validateStatusTransition(Set.of(DeliveryStatus.CREATED));
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

	private void validateStatusTransition(Set<DeliveryStatus> allowedStatuses) {
		if (!allowedStatuses.contains(this.status)) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_STATUS_TRANSITION);
		}
	}
}
