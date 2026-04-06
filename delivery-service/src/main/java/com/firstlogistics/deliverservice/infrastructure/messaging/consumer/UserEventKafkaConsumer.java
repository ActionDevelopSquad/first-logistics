package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.application.DeliveryManagerCommandService;
import com.firstlogistics.deliverservice.domain.event.UserStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventKafkaConsumer {

	private static final String DELIVERY_MANAGER_ROLE = "DELIVERY_MANAGER";
	private static final String APPROVED_STATUS = "APPROVED";

	private final DeliveryManagerCommandService deliveryManagerCommandService;

	@KafkaListener(
		topics = "user.status.changed",
		groupId = "delivery-service",
		containerFactory = "userStatusChangedListenerContainerFactory"
	)
	public void handleUserStatusChanged(UserStatusChangedEvent event, Acknowledgment ack) {
		log.info("user.status.changed 이벤트 수신 - userId: {}, role: {}, status: {}",
			event.userId(), event.userRole(), event.status());

		if (!DELIVERY_MANAGER_ROLE.equals(event.userRole())) {
			ack.acknowledge();
			return;
		}

		if (!APPROVED_STATUS.equals(event.status())) {
			ack.acknowledge();
			return;
		}

		deliveryManagerCommandService.createDeliveryManagerBySystem(event);

		ack.acknowledge();
	}
}
