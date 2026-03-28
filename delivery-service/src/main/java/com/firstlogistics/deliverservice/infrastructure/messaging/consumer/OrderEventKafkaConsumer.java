package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event.OrderAcceptedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventKafkaConsumer {

	// TODO: DeliveryCommandService 주입 후 연결

	@KafkaListener(
		topics = "order.accepted",
		groupId = "delivery-service",
		containerFactory = "orderAcceptedListenerFactory"
	)
	public void handleOrderAccepted(OrderAcceptedEvent event) {
		log.info("order.accepted 이벤트 수신 - orderId: {}", event.orderId());
		// TODO: deliveryCommandService.createDelivery(event);
	}
}
