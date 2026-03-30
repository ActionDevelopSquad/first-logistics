package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.DeliveryQueryService;
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
	private final DeliveryQueryService deliveryQueryService;

	@KafkaListener(
		topics = "order.accepted",
		groupId = "delivery-service",
		containerFactory = "orderAcceptedListenerContainerFactory"
	)
	public void handleOrderAccepted(OrderAcceptedEvent event, Acknowledgment ack) {
		log.info("order.accepted 이벤트 수신 - orderId: {}", event.orderId());

		// 중복 메시지 처리 (at-least-once): 이미 배송이 존재하면 ack 후 스킵
		if (deliveryQueryService.existsByOrderId(event.orderId())) {
			log.info("중복 메시지 무시 - orderId: {}", event.orderId());
			ack.acknowledge();
			return;
		}

		deliveryCommandService.createDelivery(new CreateDeliveryCommand(
			event.orderId(),
			event.sourceHubId(),
			event.receiverCompanyId(),
			event.receiverId(),
			event.roadAddress(),
			event.detailAddress(),
			event.latitude(),
			event.longitude()
		));
		ack.acknowledge();
	}
}
