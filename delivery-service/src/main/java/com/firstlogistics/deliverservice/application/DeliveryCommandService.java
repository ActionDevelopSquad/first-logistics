package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryResult;
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
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryCommandService {

	private final DeliveryRepository deliveryRepository;
	private final DeliveryStaffRepository deliveryStaffRepository;
	private final UserPort userPort;
	private final HubPort hubPort;
	private final DeliveryEventPublisher deliveryEventPublisher;

	public CreateDeliveryResult createDelivery(
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
		List<DeliveryStaff> hubDeliveryStaffs = new ArrayList<>();

		for (HubRouteStepResponse step : hubSteps) {
			LocalDateTime assignmentStart = now.plusMinutes(cumulativeMinutes);
			LocalDateTime assignmentEnd = now.plusMinutes(cumulativeMinutes + step.durationMinutes());

			DeliveryStaff hubDeliveryStaff = deliveryStaffRepository.findNextHubDeliveryStaff(step.sourceHubId(), assignmentStart, assignmentEnd)
				.orElseThrow(() -> new DeliveryCreationException(DeliveryErrorCode.HUB_DELIVERY_STAFF_NOT_AVAILABLE));

			hubDeliveryStaffs.add(hubDeliveryStaff);
			cumulativeMinutes += step.durationMinutes();
		}

		LocalDateTime companyAssignmentStart = now.plusMinutes(cumulativeMinutes);
		LocalDateTime companyAssignmentEnd = companyAssignmentStart.plusMinutes(lastStep.durationMinutes());

		DeliveryStaff companyDeliveryStaff = deliveryStaffRepository.findNextCompanyDeliveryStaff(destinationHubId, companyAssignmentStart, companyAssignmentEnd)
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
			companyDeliveryStaff.getId()
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
			delivery.assignRoute(route, hubDeliveryStaffs.get(i).getId());
		}

		DeliveryRoute companyRoute = DeliveryRoute.create(
			delivery.getId(),
			lastStep.hubRouteSequence(),
			lastStep.sourceHubId(),
			lastStep.destinationHubId(),
			lastStep.distanceMeters(),
			lastStep.durationMinutes()
		);
		delivery.assignRoute(companyRoute, companyDeliveryStaff.getId());

		Delivery savedDelivery = deliveryRepository.save(delivery);

		int timetableMinutes = 0;
		for (int i = 0; i < hubSteps.size(); i++) {
			HubRouteStepResponse step = hubSteps.get(i);
			LocalDateTime assignmentStart = now.plusMinutes(timetableMinutes);
			LocalDateTime assignmentEnd = now.plusMinutes(timetableMinutes + step.durationMinutes());

			hubDeliveryStaffs.get(i).assignDelivery(savedDelivery.getId(), assignmentStart, assignmentEnd);
			deliveryStaffRepository.save(hubDeliveryStaffs.get(i));

			timetableMinutes += step.durationMinutes();
		}

		companyDeliveryStaff.assignDelivery(savedDelivery.getId(), companyAssignmentStart, companyAssignmentEnd);
		deliveryStaffRepository.save(companyDeliveryStaff);

		List<UUID> hubIds = orderedRoutes.stream()
			.flatMap(step -> Stream.of(step.sourceHubId(), step.destinationHubId()))
			.distinct().toList();
		Map<UUID, HubResponse> hubMap = hubPort.getHubs(hubIds).stream()
			.collect(Collectors.toMap(HubResponse::hubId, hub -> hub));
		UserResponse companyDeliveryStaffUser = userPort.getUser(companyDeliveryStaff.getId().id());

		DeliveryCreatedEvent deliveryCreatedEvent = buildDeliveryCreatedEvent(
				savedDelivery, command, receiver, hubSteps, hubDeliveryStaffs, lastStep, companyDeliveryStaff, hubMap, companyDeliveryStaffUser
		);
		deliveryEventPublisher.publishedDeliveryCreated(deliveryCreatedEvent);

		return CreateDeliveryResult.from(savedDelivery);
	}

	private DeliveryCreatedEvent buildDeliveryCreatedEvent(
			Delivery delivery,
			CreateDeliveryCommand command,
			UserResponse receiver,
			List<HubRouteStepResponse> hubSteps,
			List<DeliveryStaff> hubDeliveryStaffs,
			HubRouteStepResponse lastStep,
			DeliveryStaff companyDeliveryStaff,
			Map<UUID, HubResponse> hubMap,
			UserResponse companyDeliveryStaffUser) {

		List<DeliveryCreatedEvent.DeliveryRouteInfo> deliveryRoutes = new ArrayList<>();
		for (int i = 0; i < hubSteps.size(); i++) {
			HubRouteStepResponse step = hubSteps.get(i);
			HubResponse sourceHub = hubMap.get(step.sourceHubId());
			HubResponse destinationHub = hubMap.get(step.destinationHubId());
			deliveryRoutes.add(DeliveryCreatedEvent.DeliveryRouteInfo.of(
				step.hubRouteSequence(),
				step.sourceHubId(), sourceHub.name(), sourceHub.roadAddress(),
				step.destinationHubId(), destinationHub.name(), destinationHub.roadAddress(),
				step.distanceMeters(),
				step.durationMinutes(),
				hubDeliveryStaffs.get(i).getSlackId()
			));
		}

		HubResponse lastSourceHub = hubMap.get(lastStep.sourceHubId());
		HubResponse lastDestinationHub = hubMap.get(lastStep.destinationHubId());
		deliveryRoutes.add(DeliveryCreatedEvent.DeliveryRouteInfo.of(
			lastStep.hubRouteSequence(),
			lastStep.sourceHubId(), lastSourceHub.name(), lastSourceHub.roadAddress(),
			lastStep.destinationHubId(), lastDestinationHub.name(), lastDestinationHub.roadAddress(),
			lastStep.distanceMeters(),
			lastStep.durationMinutes(),
			companyDeliveryStaff.getSlackId()
		));

		DeliveryCreatedEvent.OrderInfo orderInfo = DeliveryCreatedEvent.OrderInfo.of(
			delivery.getOrderId(),
			command.orderedAt(),
			command.orderDueDate(),
			command.orderRequestNote(),
			command.orderItems().stream()
				.map(item -> DeliveryCreatedEvent.OrderItemInfo.of(item.productId(), item.productName(), item.quantity(), item.price()))
				.toList()
		);

		DeliveryCreatedEvent.DeliveryInfo deliveryInfo = DeliveryCreatedEvent.DeliveryInfo.of(
			delivery.getId().id(),
			receiver.name(),
			delivery.getReceiverSlackId(),
			command.receiverRoadAddress(),
			command.receiverDetailAddress(),
			deliveryRoutes,
			companyDeliveryStaff.getSlackId(),
			companyDeliveryStaff.getStaffDetail().staffName(),
			companyDeliveryStaff.getStaffDetail().phoneNumber(),
			companyDeliveryStaffUser.email()
		);

		return DeliveryCreatedEvent.create(orderInfo, deliveryInfo);
	}
}
