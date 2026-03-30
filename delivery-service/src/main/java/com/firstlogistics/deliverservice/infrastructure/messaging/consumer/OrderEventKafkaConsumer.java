package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event.OrderAcceptedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventKafkaConsumer {

	private final DeliveryCommandService deliveryCommandService;

	@KafkaListener(
		topics = "order.accepted",
		groupId = "delivery-service",
		containerFactory = "deliveryListenerContainerFactory"
	)
	public void handleOrderAccepted(OrderAcceptedEvent event, Acknowledgment ack) {
		log.info("order.accepted 이벤트 수신 - orderId: {}", event.orderId());
		final CreateDeliveryCommand createDeliveryCommand = new CreateDeliveryCommand(
				event.orderId(),
				event.sourceHubId(),
				event.receiverCompanyId(),
				event.receiverId(),
				event.roadAddress(),
				event.detailAddress(),
				event.latitude(),
				event.longitude()
		);
		deliveryCommandService.createDelivery(createDeliveryCommand);
		ack.acknowledge();
	}
}
