package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryResult;
import com.firstlogistics.deliverservice.application.publisher.DeliveryEventPublisher;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryRoute;
import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;
import com.firstlogistics.deliverservice.domain.event.DeliveryCreatedEvent;
import com.firstlogistics.deliverservice.domain.exception.DeliveryCreationException;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryStaffRepository;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryCommandService {

	private final DeliveryRepository deliveryRepository;
	private final DeliveryStaffRepository deliveryStaffRepository;
	private final UserPort userPort;
	private final DeliveryEventPublisher deliveryEventPublisher;

	public DeliveryResult createDelivery(
			CreateDeliveryCommand command,
			CompanyResponse company,
            HubRouteResponse hubRoute) {
		if (deliveryRepository.existsByOrderId(command.orderId())) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_ALREADY_EXISTS);
		}

		if (hubRoute.routes() == null || hubRoute.routes().isEmpty()) {
			throw new DeliveryCreationException(DeliveryErrorCode.HUB_ROUTE_INVALID);
		}

		UUID destinationHubId = company.hubId();

		List<HubRouteStepResponse> orderedRoutes = hubRoute.routes().stream()
			.sorted(Comparator.comparingInt(HubRouteStepResponse::hubRouteSequence))
			.toList();

		HubRouteStepResponse lastStep = orderedRoutes.getLast();
		List<HubRouteStepResponse> hubSteps = orderedRoutes.subList(0, orderedRoutes.size() - 1);

		LocalDateTime now = LocalDateTime.now();
		int cumulativeMinutes = 0;
		List<DeliveryStaff> hubStaffs = new ArrayList<>();

		for (HubRouteStepResponse step : hubSteps) {
			LocalDateTime assignmentStart = now.plusMinutes(cumulativeMinutes);
			LocalDateTime assignmentEnd = now.plusMinutes(cumulativeMinutes + step.durationMinutes());

			DeliveryStaff hubStaff = deliveryStaffRepository.findNextHubStaff(step.sourceHubId(), assignmentStart, assignmentEnd)
				.orElseThrow(() -> new DeliveryCreationException(DeliveryErrorCode.HUB_DELIVERY_STAFF_NOT_AVAILABLE));

			hubStaffs.add(hubStaff);
			cumulativeMinutes += step.durationMinutes();
		}

		LocalDateTime companyAssignmentStart = now.plusMinutes(cumulativeMinutes);
		LocalDateTime companyAssignmentEnd = companyAssignmentStart.plusMinutes(lastStep.durationMinutes());

		DeliveryStaff companyStaff = deliveryStaffRepository.findNextCompanyStaff(destinationHubId, companyAssignmentStart, companyAssignmentEnd)
			.orElseThrow(() -> new DeliveryCreationException(DeliveryErrorCode.COMPANY_DELIVERY_STAFF_NOT_AVAILABLE));

		UserResponse receiver = userPort.getUser(command.receiverId());

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

		for (int i = 0; i < hubSteps.size(); i++) {
			HubRouteStepResponse step = hubSteps.get(i);
			DeliveryRoute route = DeliveryRoute.create(
				delivery.getId(),
				step.hubRouteSequence(),
				step.sourceHubId(),
				step.destinationHubId(),
				step.distanceMeters(),
				step.durationMinutes()
			);
			delivery.assignRoute(route, hubStaffs.get(i).getId());
		}

		Delivery savedDelivery = deliveryRepository.save(delivery);

		int timetableMinutes = 0;
		for (int i = 0; i < hubSteps.size(); i++) {
			HubRouteStepResponse step = hubSteps.get(i);
			LocalDateTime assignmentStart = now.plusMinutes(timetableMinutes);
			LocalDateTime assignmentEnd = now.plusMinutes(timetableMinutes + step.durationMinutes());

			hubStaffs.get(i).assignDelivery(savedDelivery.getId(), assignmentStart, assignmentEnd);
			deliveryStaffRepository.save(hubStaffs.get(i));

			timetableMinutes += step.durationMinutes();
		}

		companyStaff.assignDelivery(savedDelivery.getId(), companyAssignmentStart, companyAssignmentEnd);
		deliveryStaffRepository.save(companyStaff);

		deliveryEventPublisher.publishedDeliveryCreated(
				DeliveryCreatedEvent.create(savedDelivery.getId().id(), savedDelivery.getOrderId(), savedDelivery.getReceiverSlackId())
		);

		return DeliveryResult.from(savedDelivery);
	}
}
