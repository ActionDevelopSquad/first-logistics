package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;
import com.firstlogistics.deliverservice.domain.entity.StaffTimetable;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryStaffId;
import com.firstlogistics.deliverservice.domain.vo.StaffDetail;
import com.firstlogistics.deliverservice.domain.vo.StaffTimetableId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeliveryStaffMapper {

	public DeliveryStaffJpaEntity toJpaEntity(DeliveryStaff staff) {
		DeliveryStaffJpaEntity jpaEntity = DeliveryStaffJpaEntity.create(
			staff.getId().id(),
			staff.getStaffDetail().staffName(),
			staff.getStaffDetail().phoneNumber(),
			staff.getHubId(),
			staff.getSlackId(),
			staff.getStaffType(),
			staff.getDeliverySequence()
		);

		for (StaffTimetable timetable : staff.getTimetables()) {
			StaffTimetableJpaEntity timetableJpaEntity = toTimetableJpaEntity(timetable, jpaEntity);
			jpaEntity.addTimetable(timetableJpaEntity);
		}

		return jpaEntity;
	}

	public StaffTimetableJpaEntity toTimetableJpaEntity(StaffTimetable timetable, DeliveryStaffJpaEntity staffJpaEntity) {
		return StaffTimetableJpaEntity.create(
			timetable.getId().id(),
			staffJpaEntity,
			timetable.getDeliveryId().id(),
			timetable.getExpectedStartAt(),
			timetable.getExpectedEndAt(),
			timetable.getStatus()
		);
	}

	public DeliveryStaff toDomain(DeliveryStaffJpaEntity jpaEntity) {
		List<StaffTimetable> timetables = jpaEntity.getTimetables().stream()
			.map(t -> toTimetableDomain(t))
			.collect(Collectors.toCollection(ArrayList::new));

		return DeliveryStaff.reconstitute(
			DeliveryStaffId.of(jpaEntity.getId()),
			StaffDetail.of(jpaEntity.getStaffName(), jpaEntity.getPhoneNumber()),
			jpaEntity.getHubId(),
			jpaEntity.getSlackId(),
			jpaEntity.getStaffType(),
			jpaEntity.getDeliverySequence(),
			timetables
		);
	}

	public StaffTimetable toTimetableDomain(StaffTimetableJpaEntity jpaEntity) {
		return StaffTimetable.reconstitute(
			StaffTimetableId.of(jpaEntity.getId()),
			DeliveryStaffId.of(jpaEntity.getDeliveryStaff().getId()),
			DeliveryId.of(jpaEntity.getDeliveryId()),
			jpaEntity.getExpectedStartAt(),
			jpaEntity.getExpectedEndAt(),
			jpaEntity.getStatus()
		);
	}
}
