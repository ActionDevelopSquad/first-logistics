package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryJpaEntity extends BaseAuditEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid")
	private UUID id;

	@Column(name = "order_id", nullable = false, columnDefinition = "uuid")
	private UUID orderId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private DeliveryStatus status;

	@Column(name = "source_hub_id", nullable = false, columnDefinition = "uuid")
	private UUID sourceHubId;

	@Column(name = "destination_hub_id", nullable = false, columnDefinition = "uuid")
	private UUID destinationHubId;

	@Column(name = "road_address", nullable = false)
	private String roadAddress;

	@Column(name = "detail_address", nullable = false)
	private String detailAddress;

	@Column(name = "latitude", nullable = false)
	private double latitude;

	@Column(name = "longitude", nullable = false)
	private double longitude;

	@Column(name = "receiver_id", nullable = false, columnDefinition = "uuid")
	private UUID receiverId;

	@Column(name = "receiver_slack_id", nullable = false)
	private String receiverSlackId;

	@Column(name = "receiver_company_id", nullable = false, columnDefinition = "uuid")
	private UUID receiverCompanyId;

	@Column(name = "receiver_company_delivery_staff_id", columnDefinition = "uuid")
	private UUID receiverCompanyDeliveryStaffId;

	@Column(name = "current_hub_id", columnDefinition = "uuid")
	private UUID currentHubId;

	@OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<DeliveryRouteJpaEntity> routes = new ArrayList<>();

	public static DeliveryJpaEntity create(
		UUID id,
		UUID orderId,
		DeliveryStatus status,
		UUID sourceHubId,
		UUID destinationHubId,
		String roadAddress,
		String detailAddress,
		double latitude,
		double longitude,
		UUID receiverId,
		String receiverSlackId,
		UUID receiverCompanyId,
		UUID receiverCompanyDeliveryStaffId,
		UUID currentHubId
	) {
		return new DeliveryJpaEntity(
			id, orderId, status,
			sourceHubId, destinationHubId,
			roadAddress, detailAddress, latitude, longitude,
			receiverId, receiverSlackId,
			receiverCompanyId, receiverCompanyDeliveryStaffId,
			currentHubId,
			new ArrayList<>()
		);
	}

	public void addRoute(DeliveryRouteJpaEntity route) {
		this.routes.add(route);
	}
}
