package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_staff_timetable")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StaffTimetableJpaEntity extends BaseAuditEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_staff_id", nullable = false)
	private DeliveryStaffJpaEntity deliveryStaff;

	@Column(name = "delivery_id", nullable = false, columnDefinition = "uuid")
	private UUID deliveryId;

	@Column(name = "expected_start_at", nullable = false)
	private LocalDateTime expectedStartAt;

	@Column(name = "expected_end_at", nullable = false)
	private LocalDateTime expectedEndAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private TimetableStatus status;

	public static StaffTimetableJpaEntity create(
		UUID id,
		DeliveryStaffJpaEntity deliveryStaff,
		UUID deliveryId,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt,
		TimetableStatus status
	) {
		return new StaffTimetableJpaEntity(
			id, deliveryStaff, deliveryId,
			expectedStartAt, expectedEndAt, status
		);
	}
}
