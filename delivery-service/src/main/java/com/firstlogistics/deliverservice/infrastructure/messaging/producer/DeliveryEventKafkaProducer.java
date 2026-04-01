package com.firstlogistics.deliverservice.infrastructure.messaging.producer;

import com.firstlogistics.deliverservice.application.port.DeliveryEventKafkaProducerPort;
import com.firstlogistics.deliverservice.domain.event.DeliveryCreatedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryCreationFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventKafkaProducer implements DeliveryEventKafkaProducerPort {

	private static final String TOPIC_CREATED = "delivery.created";
	private static final String TOPIC_CREATION_FAILED = "delivery.creation.failed";
	private static final String TOPIC_ORDER_ACCEPTED_DLT = "order.accepted.DLT";

	private final KafkaTemplate<String, Object> deliveryKafkaTemplate;

	@Override
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleDeliveryCreated(DeliveryCreatedEvent event) {
		deliveryKafkaTemplate.send(TOPIC_CREATED, event.delivery().deliveryId().toString(), event);
		log.info("이벤트 발행 - topic: {}, deliveryId: {}", TOPIC_CREATED, event.delivery().deliveryId());
	}

	@Override
	public void handleDeliveryCreationFailed(DeliveryCreationFailedEvent event) {
		deliveryKafkaTemplate.send(TOPIC_CREATION_FAILED, event.orderId().toString(), event);
		log.warn("이벤트 발행 - topic: {}, orderId: {}", TOPIC_CREATION_FAILED, event.orderId());
	}

	@Override
	public void handleOrderAcceptedDlt(String key, Object value) {
		deliveryKafkaTemplate.send(TOPIC_ORDER_ACCEPTED_DLT, key, value);
		log.warn("DLT 적재 - topic: {}, key: {}", TOPIC_ORDER_ACCEPTED_DLT, key);
	}
}
