package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_delivery_manager")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryManagerJpaEntity extends BaseAuditEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid")
	private UUID id;

	@Column(name = "user_id", nullable = false, columnDefinition = "uuid", unique = true)
	private UUID userId;

	@Column(name = "manager_name", nullable = false)
	private String managerName;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Column(name = "hub_id", nullable = false, columnDefinition = "uuid")
	private UUID hubId;

	@Column(name = "slack_id", nullable = false)
	private String slackId;

	@Enumerated(EnumType.STRING)
	@Column(name = "manager_type", nullable = false)
	private ManagerType managerType;

	@Column(name = "delivery_sequence", nullable = false, unique = true)
	private int deliverySequence;

	@Version
	@Column(name = "version")
	private Long version;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "delivery_manager_id", nullable = false)
	private List<ManagerTimetableJpaEntity> timetables = new ArrayList<>();

	public static DeliveryManagerJpaEntity create(
		UUID id,
		UUID userId,
		String managerName,
		String phoneNumber,
		UUID hubId,
		String slackId,
		ManagerType managerType,
		int deliverySequence
	) {
		return new DeliveryManagerJpaEntity(
			id, userId, managerName, phoneNumber,
			hubId, slackId, managerType,
			deliverySequence, null, new ArrayList<>()
		);
	}

	public void addTimetable(ManagerTimetableJpaEntity timetable) {
		this.timetables.add(timetable);
	}

	public void update(com.firstlogistics.deliverservice.domain.entity.DeliveryManager domain, DeliveryManagerMapper mapper) {
		this.hubId = domain.getHubId();
		this.managerType = domain.getManagerType();

		for (com.firstlogistics.deliverservice.domain.entity.ManagerTimetable timetable : domain.getTimetables()) {
			boolean exists = this.timetables.stream()
				.anyMatch(existing -> existing.getId().equals(timetable.getId().id()));
			if (!exists) {
				this.timetables.add(mapper.toTimetableJpaEntity(timetable));
			}
		}
	}
}
