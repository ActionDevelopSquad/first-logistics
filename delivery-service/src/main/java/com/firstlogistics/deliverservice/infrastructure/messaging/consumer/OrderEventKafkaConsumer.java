package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.application.DeliveryQueryService;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.facade.DeliveryCommandFacade;
import com.firstlogistics.deliverservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.deliverservice.domain.event.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventKafkaConsumer {

	private final DeliveryQueryService deliveryQueryService;
	private final DeliveryCommandFacade deliveryCommandFacade;

	@KafkaListener(
		topics = "order.accepted",
		groupId = "delivery-service",
		containerFactory = "orderAcceptedListenerContainerFactory"
	)
	public void handleOrderAccepted(OrderAcceptedEvent event, Acknowledgment ack) {
		log.info("order.accepted 이벤트 수신 - orderId: {}", event.orderId());

		if (deliveryQueryService.existsByOrderId(event.orderId())) {
			log.info("중복 메시지 무시 - orderId: {}", event.orderId());
			ack.acknowledge();
			return;
		}

		deliveryCommandFacade.createDeliveryBySystem(CreateDeliveryCommand.from(event));

		ack.acknowledge();
	}

	@KafkaListener(
		topics = "order.cancelled",
		groupId = "delivery-service",
		containerFactory = "orderCancelledListenerContainerFactory"
	)
	public void handleOrderCancelled(OrderCancelledEvent event, Acknowledgment ack) {
		log.info("order.cancelled 이벤트 수신 - orderId: {}", event.orderId());

		// 배송 취소(배송 출발 전 취소) 추후 고도화
//		deliveryCommandFacade.cancelDeliveryBySystem(event.orderId());

		ack.acknowledge();
	}
}
