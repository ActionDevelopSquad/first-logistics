package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.entity.ManagerTimetable;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import com.firstlogistics.deliverservice.domain.vo.ManagerDetail;
import com.firstlogistics.deliverservice.domain.vo.ManagerTimetableId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeliveryManagerMapper {

	public DeliveryManagerJpaEntity toJpaEntity(DeliveryManager manager) {
		DeliveryManagerJpaEntity jpaEntity = DeliveryManagerJpaEntity.create(
			manager.getId().id(),
			manager.getUserId(),
			manager.getManagerDetail().managerName(),
			manager.getManagerDetail().phoneNumber(),
			manager.getHubId(),
			manager.getSlackId(),
			manager.getManagerType(),
			manager.getDeliverySequence()
		);

		for (ManagerTimetable timetable : manager.getTimetables()) {
			ManagerTimetableJpaEntity timetableJpaEntity = toTimetableJpaEntity(timetable);
			jpaEntity.addTimetable(timetableJpaEntity);
		}

		return jpaEntity;
	}

	public ManagerTimetableJpaEntity toTimetableJpaEntity(ManagerTimetable timetable) {
		return ManagerTimetableJpaEntity.create(
			timetable.getId().id(),
			timetable.getManagerId().id(),
			timetable.getDeliveryId().id(),
			timetable.getExpectedStartAt(),
			timetable.getExpectedEndAt(),
			timetable.getStatus()
		);
	}

	public DeliveryManager toDomain(DeliveryManagerJpaEntity jpaEntity) {
		List<ManagerTimetable> timetables = jpaEntity.getTimetables().stream()
			.map(t -> toTimetableDomain(t))
			.collect(Collectors.toCollection(ArrayList::new));

		return DeliveryManager.reconstitute(
			DeliveryManagerId.of(jpaEntity.getId()),
			jpaEntity.getUserId(),
			ManagerDetail.of(jpaEntity.getManagerName(), jpaEntity.getPhoneNumber()),
			jpaEntity.getHubId(),
			jpaEntity.getSlackId(),
			jpaEntity.getManagerType(),
			jpaEntity.getDeliverySequence(),
			timetables
		);
	}

	public ManagerTimetable toTimetableDomain(ManagerTimetableJpaEntity jpaEntity) {
		return ManagerTimetable.reconstitute(
			ManagerTimetableId.of(jpaEntity.getId()),
			DeliveryManagerId.of(jpaEntity.getDeliveryManagerId()),
			DeliveryId.of(jpaEntity.getDeliveryId()),
			jpaEntity.getExpectedStartAt(),
			jpaEntity.getExpectedEndAt(),
			jpaEntity.getStatus()
		);
	}
}
