package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryResult;
import com.firstlogistics.deliverservice.application.port.DeliveryEventProducer;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryRoute;
import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;
import com.firstlogistics.deliverservice.domain.entity.StaffTimetable;
import com.firstlogistics.deliverservice.domain.exception.DeliveryCreationException;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryStaffRepository;
import com.firstlogistics.deliverservice.infrastructure.feign.CompanyClient;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import com.firstlogistics.deliverservice.infrastructure.feign.UserClient;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.CompanyResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.UserResponse;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.event.DeliveryCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryCommandService {

	private final DeliveryRepository deliveryRepository;
	private final DeliveryStaffRepository deliveryStaffRepository;
	private final CompanyClient companyClient;
	private final HubClient hubClient;
	private final UserClient userClient;
	private final DeliveryEventProducer deliveryEventProducer;

	public DeliveryResult createDelivery(CreateDeliveryCommand command) {
		if (deliveryRepository.existsByOrderId(command.orderId())) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_ALREADY_EXISTS);
		}

		CompanyResponse company = companyClient.getCompany(command.receiverCompanyId()).data();
		UUID destinationHubId = company.hubId();

		HubRouteResponse hubRoute = hubClient.getHubRoute(command.sourceHubId(), destinationHubId).data();

		// TODO: [동시성] findNextHubStaff → save 사이 레이스 컨디션 존재. 분산락(Redis) 적용 필요 - 락 키: hub:staff:assign:{hubId}
		LocalDateTime now = LocalDateTime.now();
		int cumulativeMinutes = 0;
		List<DeliveryStaff> hubStaffs = new ArrayList<>();

		for (HubRouteStepResponse step : hubRoute.routes()) {
			LocalDateTime assignmentStart = now.plusMinutes(cumulativeMinutes);
			LocalDateTime assignmentEnd = now.plusMinutes(cumulativeMinutes + step.durationMinutes());

			DeliveryStaff hubStaff = deliveryStaffRepository.findNextHubStaff(step.sourceHubId(), assignmentStart, assignmentEnd)
				.orElseThrow(() -> new DeliveryCreationException(DeliveryErrorCode.HUB_DELIVERY_STAFF_NOT_AVAILABLE));

			hubStaffs.add(hubStaff);
			cumulativeMinutes += step.durationMinutes();
		}

		// TODO: [동시성] findNextCompanyStaff → save 사이 레이스 컨디션 존재. 분산락(Redis) 적용 필요 - 락 키: hub:staff:assign:{hubId}
		int lastStepDuration = hubRoute.routes().getLast().durationMinutes();
		LocalDateTime companyAssignmentStart = now.plusMinutes(cumulativeMinutes);
		LocalDateTime companyAssignmentEnd = companyAssignmentStart.plusMinutes(lastStepDuration);

		DeliveryStaff companyStaff = deliveryStaffRepository.findNextCompanyStaff(destinationHubId, companyAssignmentStart, companyAssignmentEnd)
			.orElseThrow(() -> new DeliveryCreationException(DeliveryErrorCode.COMPANY_DELIVERY_STAFF_NOT_AVAILABLE));

		UserResponse receiver = userClient.getUser(command.receiverId()).data();

		Delivery delivery = Delivery.create(
			command.orderId(),
			command.sourceHubId(),
			destinationHubId,
			command.roadAddress(),
			command.detailAddress(),
			command.latitude(),
			command.longitude(),
			command.receiverId(),
			receiver.slackId(),
			command.receiverCompanyId(),
			companyStaff.getId()
		);

		for (int i = 0; i < hubRoute.routes().size(); i++) {
			HubRouteStepResponse step = hubRoute.routes().get(i);
			DeliveryRoute route = DeliveryRoute.create(
				delivery.getId(),
				i,
				step.sourceHubId(),
				step.destinationHubId(),
				step.distanceMeters(),
				step.durationMinutes()
			);
			route.assignStaff(hubStaffs.get(i).getId());
			delivery.assignRoute(route);
		}

		Delivery savedDelivery = deliveryRepository.save(delivery);

		int timetableMinutes = 0;
		for (int i = 0; i < hubRoute.routes().size(); i++) {
			HubRouteStepResponse step = hubRoute.routes().get(i);
			LocalDateTime assignmentStart = now.plusMinutes(timetableMinutes);
			LocalDateTime assignmentEnd = now.plusMinutes(timetableMinutes + step.durationMinutes());

			StaffTimetable timetable = StaffTimetable.create(hubStaffs.get(i).getId(), savedDelivery.getId(), assignmentStart, assignmentEnd);
			hubStaffs.get(i).addTimetable(timetable);
			deliveryStaffRepository.save(hubStaffs.get(i));

			timetableMinutes += step.durationMinutes();
		}

		StaffTimetable companyTimetable = StaffTimetable.create(companyStaff.getId(), savedDelivery.getId(), companyAssignmentStart, companyAssignmentEnd);
		companyStaff.addTimetable(companyTimetable);
		deliveryStaffRepository.save(companyStaff);

		final DeliveryCreatedEvent deliveryCreatedEvent = DeliveryCreatedEvent
				.create(savedDelivery.getId().id(), savedDelivery.getOrderId(), savedDelivery.getReceiverSlackId());
		deliveryEventProducer.sendCreated(deliveryCreatedEvent);

		return DeliveryResult.from(savedDelivery);
	}
}
