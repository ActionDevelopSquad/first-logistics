package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_delivery_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryRouteJpaEntity extends BaseAuditEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid")
	private UUID id;

	@Column(name = "delivery_id", nullable = false, columnDefinition = "uuid", insertable = false, updatable = false)
	private UUID deliveryId;

	@Column(name = "sequence", nullable = false)
	private int deliveryRouteSequence;

	@Column(name = "source_hub_id", nullable = false, columnDefinition = "uuid")
	private UUID sourceHubId;

	@Column(name = "destination_hub_id", nullable = false, columnDefinition = "uuid")
	private UUID destinationHubId;

	@Column(name = "estimated_distance", nullable = false)
	private int estimatedDistance;

	@Column(name = "estimated_duration", nullable = false)
	private int estimatedDuration;

	@Column(name = "actual_distance", nullable = false)
	private int actualDistance;

	@Column(name = "actual_duration", nullable = false)
	private int actualDuration;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private RouteStatus status;

	@Column(name = "delivery_manager_id", columnDefinition = "uuid")
	private UUID deliveryManagerId;

	public static DeliveryRouteJpaEntity create(
		UUID id,
		UUID deliveryId,
		int deliveryRouteSequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistance,
		int estimatedDuration,
		int actualDistance,
		int actualDuration,
		RouteStatus status,
		UUID deliveryManagerId
	) {
		return new DeliveryRouteJpaEntity(
			id, deliveryId, deliveryRouteSequence,
			sourceHubId, destinationHubId,
			estimatedDistance, estimatedDuration,
			actualDistance, actualDuration,
			status, deliveryManagerId
		);
	}
}
