package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.enums.StaffType;
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
@Table(name = "p_delivery_staff")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryStaffJpaEntity extends BaseAuditEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid")
	private UUID id;

	@Column(name = "staff_name", nullable = false)
	private String staffName;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Column(name = "hub_id", nullable = false, columnDefinition = "uuid")
	private UUID hubId;

	@Column(name = "slack_id", nullable = false)
	private String slackId;

	@Enumerated(EnumType.STRING)
	@Column(name = "staff_type", nullable = false)
	private StaffType staffType;

	@Column(name = "delivery_sequence", nullable = false)
	private int deliverySequence;

	@OneToMany(mappedBy = "deliveryStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<StaffTimetableJpaEntity> timetables = new ArrayList<>();

	public static DeliveryStaffJpaEntity create(
		UUID id,
		String staffName,
		String phoneNumber,
		UUID hubId,
		String slackId,
		StaffType staffType,
		int deliverySequence
	) {
		return new DeliveryStaffJpaEntity(
			id, staffName, phoneNumber,
			hubId, slackId, staffType,
			deliverySequence, new ArrayList<>()
		);
	}

	public void addTimetable(StaffTimetableJpaEntity timetable) {
		this.timetables.add(timetable);
	}
}
