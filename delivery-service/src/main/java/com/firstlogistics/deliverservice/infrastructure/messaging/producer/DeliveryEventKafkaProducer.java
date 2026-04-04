package com.firstlogistics.deliverservice.infrastructure.messaging.producer;

import com.firstlogistics.deliverservice.domain.event.DeliveryCreatedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryCreationFailedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryStatusChangedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventKafkaProducer {

	private static final String TOPIC_CREATED = "delivery.created";
	private static final String TOPIC_UPDATED = "delivery.updated";
	private static final String TOPIC_CREATION_FAILED = "delivery.creation.failed";
	private static final String TOPIC_STATUS_CHANGED = "delivery.status.changed";
	private static final String TOPIC_ORDER_ACCEPTED_DLT = "order.accepted.DLT";
	private static final String TOPIC_ORDER_CANCELLED_DLT = "order.cancelled.DLT";
	private static final String TOPIC_USER_STATUS_CHANGED_DLT = "user.status.changed.DLT";

	private final KafkaTemplate<String, Object> deliveryKafkaTemplate;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleDeliveryCreated(DeliveryCreatedEvent event) {
		deliveryKafkaTemplate.send(TOPIC_CREATED, event.delivery().deliveryId().toString(), event);
		log.info("이벤트 발행 - topic: {}, deliveryId: {}", TOPIC_CREATED, event.delivery().deliveryId());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleDeliveryUpdated(DeliveryUpdatedEvent event) {
		deliveryKafkaTemplate.send(TOPIC_UPDATED, event.deliveryId().toString(), event);
		log.info("이벤트 발행 - topic: {}, deliveryId: {}", TOPIC_UPDATED, event.deliveryId());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
		deliveryKafkaTemplate.send(TOPIC_STATUS_CHANGED, event.deliveryId().toString(), event);
		log.info("이벤트 발행 - topic: {}, deliveryId: {}, status: {}", TOPIC_STATUS_CHANGED, event.deliveryId(), event.status());
	}

	public void handleDeliveryCreationFailed(DeliveryCreationFailedEvent event) {
		deliveryKafkaTemplate.send(TOPIC_CREATION_FAILED, event.orderId().toString(), event);
		log.warn("이벤트 발행 - topic: {}, orderId: {}", TOPIC_CREATION_FAILED, event.orderId());
	}

	public void handleOrderAcceptedDlt(String key, Object value) {
		deliveryKafkaTemplate.send(TOPIC_ORDER_ACCEPTED_DLT, key, value);
		log.warn("DLT 적재 - topic: {}, key: {}", TOPIC_ORDER_ACCEPTED_DLT, key);
	}

	public void handleOrderCancelledDlt(String key, Object value) {
		deliveryKafkaTemplate.send(TOPIC_ORDER_CANCELLED_DLT, key, value);
		log.warn("DLT 적재 - topic: {}, key: {}", TOPIC_ORDER_CANCELLED_DLT, key);
	}

	public void handleUserStatusChangedDlt(String key, Object value) {
		deliveryKafkaTemplate.send(TOPIC_USER_STATUS_CHANGED_DLT, key, value);
		log.warn("DLT 적재 - topic: {}, key: {}", TOPIC_USER_STATUS_CHANGED_DLT, key);
	}
}
