package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.ChangeDeliveryStatusCommand;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.command.UpdateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.ChangeDeliveryStatusResult;
import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryResult;
import com.firstlogistics.deliverservice.application.dto.result.UpdateDeliveryResult;
import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;
import com.firstlogistics.deliverservice.application.permission.DeliveryPermissionValidator;
import com.firstlogistics.deliverservice.application.publisher.DeliveryEventPublisher;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryRoute;
import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.domain.event.DeliveryCreatedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryStatusChangedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryUpdatedEvent;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
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
	private final DeliveryManagerRepository deliveryManagerRepository;
	private final DeliveryPermissionValidator deliveryPermissionValidator;
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
			throw new DeliveryException(DeliveryErrorCode.HUB_ROUTE_INVALID);
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
		List<DeliveryManager> hubDeliveryManagers = new ArrayList<>();

		for (HubRouteStepResponse step : hubSteps) {
			LocalDateTime assignmentStart = now.plusMinutes(cumulativeMinutes);
			LocalDateTime assignmentEnd = now.plusMinutes(cumulativeMinutes + step.durationMinutes());

			DeliveryManager hubDeliveryManager = deliveryManagerRepository.findNextHubDeliveryManager(step.sourceHubId(), assignmentStart, assignmentEnd)
				.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.HUB_DELIVERY_MANAGER_NOT_AVAILABLE));

			hubDeliveryManagers.add(hubDeliveryManager);
			cumulativeMinutes += step.durationMinutes();
		}

		LocalDateTime companyAssignmentStart = now.plusMinutes(cumulativeMinutes);
		LocalDateTime companyAssignmentEnd = companyAssignmentStart.plusMinutes(lastStep.durationMinutes());

		DeliveryManager companyDeliveryManager = deliveryManagerRepository.findNextCompanyDeliveryManager(destinationHubId, companyAssignmentStart, companyAssignmentEnd)
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.COMPANY_DELIVERY_MANAGER_NOT_AVAILABLE));

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
			companyDeliveryManager.getId()
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
			delivery.assignRoute(route, hubDeliveryManagers.get(i).getId());
		}

		DeliveryRoute companyRoute = DeliveryRoute.create(
			delivery.getId(),
			lastStep.hubRouteSequence(),
			lastStep.sourceHubId(),
			lastStep.destinationHubId(),
			lastStep.distanceMeters(),
			lastStep.durationMinutes()
		);
		delivery.assignRoute(companyRoute, companyDeliveryManager.getId());

		Delivery savedDelivery = deliveryRepository.save(delivery);

		int timetableMinutes = 0;
		for (int i = 0; i < hubSteps.size(); i++) {
			HubRouteStepResponse step = hubSteps.get(i);
			LocalDateTime assignmentStart = now.plusMinutes(timetableMinutes);
			LocalDateTime assignmentEnd = now.plusMinutes(timetableMinutes + step.durationMinutes());

			hubDeliveryManagers.get(i).assignDelivery(savedDelivery.getId(), assignmentStart, assignmentEnd);
			deliveryManagerRepository.save(hubDeliveryManagers.get(i));

			timetableMinutes += step.durationMinutes();
		}

		companyDeliveryManager.assignDelivery(savedDelivery.getId(), companyAssignmentStart, companyAssignmentEnd);
		deliveryManagerRepository.save(companyDeliveryManager);

		List<UUID> hubIds = orderedRoutes.stream()
			.flatMap(step -> Stream.of(step.sourceHubId(), step.destinationHubId()))
			.distinct().toList();
		Map<UUID, HubResponse> hubMap = hubPort.getHubs(hubIds).stream()
			.collect(Collectors.toMap(HubResponse::hubId, hub -> hub));
		if (!hubMap.keySet().containsAll(hubIds)) {
			throw new DeliveryException(DeliveryErrorCode.HUB_NOT_FOUND);
		}
		UserResponse companyDeliveryManagerUser = userPort.getUser(companyDeliveryManager.getUserId());

		DeliveryCreatedEvent deliveryCreatedEvent = buildDeliveryCreatedEvent(
				savedDelivery, command, receiver, hubSteps, hubDeliveryManagers, lastStep, companyDeliveryManager, hubMap, companyDeliveryManagerUser
		);
		deliveryEventPublisher.publishedDeliveryCreated(deliveryCreatedEvent);

		return CreateDeliveryResult.from(savedDelivery);
	}

	public UpdateDeliveryResult updateDelivery(UpdateDeliveryCommand command) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
		deliveryPermissionValidator.validate(accessContext, command.role(), command.userId(),
			UserRole.OPERATORS);

		delivery.reassignReceiver(command.receiverId(), command.receiverSlackId());
		Delivery savedDelivery = deliveryRepository.save(delivery);

		DeliveryUpdatedEvent deliveryUpdatedEvent = DeliveryUpdatedEvent.create(
			command.deliveryId(), savedDelivery.getReceiverId(), savedDelivery.getReceiverSlackId()
		);
		deliveryEventPublisher.publishDeliveryUpdated(deliveryUpdatedEvent);

		return UpdateDeliveryResult.from(savedDelivery);
	}

	public ChangeDeliveryStatusResult startHubDelivery(ChangeDeliveryStatusCommand command, String role, UUID userId) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
		deliveryPermissionValidator.validate(accessContext, role, userId,
			UserRole.OPERATORS);

		delivery.startHubDelivery();
		Delivery savedDelivery = deliveryRepository.save(delivery);

		deliveryEventPublisher.publishDeliveryStatusChanged(
			DeliveryStatusChangedEvent.create(savedDelivery));

		return ChangeDeliveryStatusResult.from(savedDelivery);
	}

	public ChangeDeliveryStatusResult startCompanyDelivery(ChangeDeliveryStatusCommand command, String role, UUID userId) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
		deliveryPermissionValidator.validate(accessContext, role, userId,
			UserRole.OPERATORS);

		delivery.startCompanyDelivery();
		Delivery savedDelivery = deliveryRepository.save(delivery);

		deliveryEventPublisher.publishDeliveryStatusChanged(
			DeliveryStatusChangedEvent.create(savedDelivery));

		return ChangeDeliveryStatusResult.from(savedDelivery);
	}

	public ChangeDeliveryStatusResult arriveHub(ChangeDeliveryStatusCommand command, String role, UUID userId) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
		deliveryPermissionValidator.validate(accessContext, role, userId,
			UserRole.MASTER_AND_DELIVERY);

		delivery.arriveAtHub();
		Delivery savedDelivery = deliveryRepository.save(delivery);

		deliveryEventPublisher.publishDeliveryStatusChanged(
			DeliveryStatusChangedEvent.create(savedDelivery));

		return ChangeDeliveryStatusResult.from(savedDelivery);
	}

	public ChangeDeliveryStatusResult receiveAtHub(ChangeDeliveryStatusCommand command, String role, UUID userId) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
		deliveryPermissionValidator.validate(accessContext, role, userId,
			UserRole.MANAGERS);

		delivery.receiveAtHub();
		Delivery savedDelivery = deliveryRepository.save(delivery);

		deliveryEventPublisher.publishDeliveryStatusChanged(
			DeliveryStatusChangedEvent.create(savedDelivery));

		return ChangeDeliveryStatusResult.from(savedDelivery);
	}

	public ChangeDeliveryStatusResult completeDelivery(ChangeDeliveryStatusCommand command, String role, UUID userId) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
		deliveryPermissionValidator.validate(accessContext, role, userId,
			UserRole.MASTER_AND_DELIVERY);

		delivery.completeDelivery();
		Delivery savedDelivery = deliveryRepository.save(delivery);

		deliveryEventPublisher.publishDeliveryStatusChanged(
			DeliveryStatusChangedEvent.create(savedDelivery));

		return ChangeDeliveryStatusResult.from(savedDelivery);
	}

	public ChangeDeliveryStatusResult cancelDelivery(ChangeDeliveryStatusCommand command, String role, UUID userId) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
		deliveryPermissionValidator.validate(accessContext, role, userId,
			UserRole.MANAGERS);

		delivery.cancelDelivery();
		Delivery savedDelivery = deliveryRepository.save(delivery);

		deliveryEventPublisher.publishDeliveryStatusChanged(
			DeliveryStatusChangedEvent.create(savedDelivery));

		return ChangeDeliveryStatusResult.from(savedDelivery);
	}

	public void cancelDeliveryBySystem(ChangeDeliveryStatusCommand command) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		if (delivery.getStatus() == DeliveryStatus.CANCELLED) {
			return;
		}

		delivery.cancelDelivery();
		Delivery savedDelivery = deliveryRepository.save(delivery);

		deliveryEventPublisher.publishDeliveryStatusChanged(
			DeliveryStatusChangedEvent.create(savedDelivery));
	}

	public void deleteDelivery(UUID deliveryId, String role, UUID userId) {
		Delivery delivery = deliveryRepository.findById(DeliveryId.of(deliveryId))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

		DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
		deliveryPermissionValidator.validate(accessContext, role, userId,
			UserRole.MANAGERS);

		deliveryRepository.deleteById(DeliveryId.of(deliveryId), userId);
	}

	private DeliveryCreatedEvent buildDeliveryCreatedEvent(
			Delivery delivery,
			CreateDeliveryCommand command,
			UserResponse receiver,
			List<HubRouteStepResponse> hubSteps,
			List<DeliveryManager> hubDeliveryManagers,
			HubRouteStepResponse lastStep,
			DeliveryManager companyDeliveryManager,
			Map<UUID, HubResponse> hubMap,
			UserResponse companyDeliveryManagerUser) {

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
				hubDeliveryManagers.get(i).getSlackId()
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
			companyDeliveryManager.getSlackId()
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
			delivery.getCurrentHubId(),
			receiver.name(),
			delivery.getReceiverSlackId(),
			receiver.email(),
			receiver.phone(),
			command.receiverRoadAddress(),
			command.receiverDetailAddress(),
			deliveryRoutes,
			companyDeliveryManager.getSlackId(),
			companyDeliveryManager.getManagerDetail().managerName(),
			companyDeliveryManager.getManagerDetail().phoneNumber(),
			companyDeliveryManagerUser.email()
		);

		return DeliveryCreatedEvent.create(orderInfo, deliveryInfo);
	}
}
