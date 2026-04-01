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
			CompanyResponse supplierCompany,
			CompanyResponse receiverCompany,
			HubRouteResponse hubRoute) {
		if (deliveryRepository.existsByOrderId(command.orderId())) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_ALREADY_EXISTS);
		}

		if (hubRoute.routes() == null || hubRoute.routes().isEmpty()) {
			throw new DeliveryCreationException(DeliveryErrorCode.HUB_ROUTE_INVALID);
		}

		UUID sourceHubId = supplierCompany.hubId();
		UUID destinationHubId = receiverCompany.hubId();

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

		UserResponse receiver = userPort.getUser(command.receiverManagerId());

		Delivery delivery = Delivery.create(
			command.orderId(),
			sourceHubId,
			destinationHubId,
			command.receiverRoadAddress(),
			command.receiverDetailAddress(),
			command.receiverManagerId(),
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

		DeliveryRoute companyRoute = DeliveryRoute.create(
			delivery.getId(),
			lastStep.hubRouteSequence(),
			lastStep.sourceHubId(),
			lastStep.destinationHubId(),
			lastStep.distanceMeters(),
			lastStep.durationMinutes()
		);
		delivery.assignRoute(companyRoute, companyStaff.getId());

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

		DeliveryCreatedEvent deliveryCreatedEvent = buildDeliveryCreatedEvent(
				savedDelivery, command, receiver, hubSteps, hubStaffs, lastStep, companyStaff
		);
		deliveryEventPublisher.publishedDeliveryCreated(deliveryCreatedEvent);

		return DeliveryResult.from(savedDelivery, command, receiver.name());
	}

	private DeliveryCreatedEvent buildDeliveryCreatedEvent(
			Delivery delivery,
			CreateDeliveryCommand command,
			UserResponse receiver,
			List<HubRouteStepResponse> hubSteps,
			List<DeliveryStaff> hubStaffs,
			HubRouteStepResponse lastStep,
			DeliveryStaff companyStaff) {

		List<DeliveryCreatedEvent.DeliveryRouteInfo> deliveryRoutes = new ArrayList<>();
		for (int i = 0; i < hubSteps.size(); i++) {
			HubRouteStepResponse step = hubSteps.get(i);
			deliveryRoutes.add(DeliveryCreatedEvent.DeliveryRouteInfo.of(
				step.hubRouteSequence(),
				step.sourceHubId(),
				step.destinationHubId(),
				step.distanceMeters(),
				step.durationMinutes(),
				hubStaffs.get(i).getSlackId()
			));
		}
		deliveryRoutes.add(DeliveryCreatedEvent.DeliveryRouteInfo.of(
			lastStep.hubRouteSequence(),
			lastStep.sourceHubId(),
			lastStep.destinationHubId(),
			lastStep.distanceMeters(),
			lastStep.durationMinutes(),
			companyStaff.getSlackId()
		));

		DeliveryCreatedEvent.OrderInfo orderInfo = DeliveryCreatedEvent.OrderInfo.of(
			delivery.getOrderId(),
			command.orderedAt(),
			command.orderDueDate(),
			command.orderRequestNote(),
			command.orderItems().stream()
				.map(i -> DeliveryCreatedEvent.OrderItemInfo.of(i.productId(), i.productName(), i.quantity(), i.price()))
				.toList()
		);

		DeliveryCreatedEvent.DeliveryInfo deliveryInfo = DeliveryCreatedEvent.DeliveryInfo.of(
			delivery.getId().id(),
			receiver.name(),
			delivery.getReceiverSlackId(),
			command.receiverRoadAddress(),
			command.receiverDetailAddress(),
			deliveryRoutes,
			companyStaff.getSlackId()
		);

		return DeliveryCreatedEvent.create(orderInfo, deliveryInfo);
	}
}
