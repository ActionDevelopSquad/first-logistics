package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryManagerCommand;
import com.firstlogistics.deliverservice.application.dto.command.UpdateDeliveryManagerCommand;
import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryManagerResult;
import com.firstlogistics.deliverservice.application.dto.result.UpdateDeliveryManagerResult;
import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.event.UserStatusChangedEvent;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerCommandService {

	private final DeliveryManagerRepository deliveryManagerRepository;
	private final HubManagerPort hubManagerPort;
	private final UserPort userPort;

	public CreateDeliveryManagerResult createDeliveryManager(
		CreateDeliveryManagerCommand command, UserRole role, UUID requestUserId
	) {
		log.info("[배송담당자 생성] 시작 - targetUserId: {}, hubId: {}, type: {}", command.userId(), command.hubId(), command.managerType());
		if (role == UserRole.HUB_MANAGER) {
			HubManagerResponse hubManager = hubManagerPort.getHubManager(requestUserId);
			if (!hubManager.hubId().equals(command.hubId())) {
				throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
			}
		}

		if (deliveryManagerRepository.existsByUserId(command.userId())) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_ALREADY_EXISTS);
		}

		UserResponse user = userPort.getUser(command.userId());

		int nextSequence = deliveryManagerRepository.findNextSequence();
		DeliveryManager deliveryManager = DeliveryManager.create(
			command.userId(), user.name(), user.phone(),
			command.hubId(), user.slackId(),
			command.managerType(), nextSequence
		);

		DeliveryManager saved = deliveryManagerRepository.save(deliveryManager);
		return CreateDeliveryManagerResult.from(saved);
	}


	public UpdateDeliveryManagerResult updateDeliveryManager(
		UpdateDeliveryManagerCommand command, UserRole role, UUID requestUserId
	) {
		log.info("[배송담당자 수정] 시작 - managerId: {}", command.managerId());
		DeliveryManager manager = deliveryManagerRepository.findById(DeliveryManagerId.of(command.managerId()))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND));

		if (role == UserRole.HUB_MANAGER) {
			UUID hubId = hubManagerPort.getHubManager(requestUserId).hubId();
			if (!hubId.equals(manager.getHubId())) {
				throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
			}
		}

		manager.reassign(command.hubId(), command.managerType());
		DeliveryManager saved = deliveryManagerRepository.save(manager);
		return UpdateDeliveryManagerResult.from(saved);
	}

	public void deleteDeliveryManager(UUID managerId, UserRole role, UUID requestUserId) {
		log.info("[배송담당자 삭제] 시작 - managerId: {}", managerId);
		DeliveryManager manager = deliveryManagerRepository.findById(DeliveryManagerId.of(managerId))
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND));

		if (role == UserRole.HUB_MANAGER) {
			UUID hubId = hubManagerPort.getHubManager(requestUserId).hubId();
			if (!hubId.equals(manager.getHubId())) {
				throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
			}
		}

		manager.validateDeletable();
		deliveryManagerRepository.deleteById(DeliveryManagerId.of(managerId), requestUserId);
	}

	public void createDeliveryManagerBySystem(UserStatusChangedEvent event) {
		if (deliveryManagerRepository.existsByUserId(event.userId())) {
			log.info("중복 배송담당자 무시 - userId: {}", event.userId());
			return;
		}

		ManagerType managerType = parseManagerType(event.managerType());

		int nextSequence = deliveryManagerRepository.findNextSequence();
		DeliveryManager deliveryManager = DeliveryManager.create(
			event.userId(), event.name(), event.phone(),
			event.hubId(), event.slackId(),
			managerType, nextSequence
		);

		deliveryManagerRepository.save(deliveryManager);
		log.info("배송담당자 자동 생성 - userId: {}, hubId: {}, type: {}", event.userId(), event.hubId(), managerType);
	}

	private ManagerType parseManagerType(String managerType) {
		if (managerType == null || managerType.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_MANAGER_PARAMS);
		}
		try {
			return ManagerType.valueOf(managerType);
		} catch (IllegalArgumentException exception) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_MANAGER_PARAMS);
		}
	}
}
