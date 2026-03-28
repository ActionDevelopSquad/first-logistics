package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

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

	// TODO: DeliveryCommandService 주입 후 연결

	@KafkaListener(
		topics = "order.accepted",
		groupId = "delivery-service",
		containerFactory = "deliveryListenerContainerFactory"
	)
	public void handleOrderAccepted(OrderAcceptedEvent event, Acknowledgment ack) {
		log.info("order.accepted 이벤트 수신 - orderId: {}", event.orderId());
		try {
			// TODO: deliveryCommandService.createDelivery(event);
			ack.acknowledge();
		} catch (Exception e) {
			log.error("order.accepted 이벤트 처리 실패 - orderId: {}", event.orderId(), e);
			// ack 하지 않으면 재시도
		}
	}
}
