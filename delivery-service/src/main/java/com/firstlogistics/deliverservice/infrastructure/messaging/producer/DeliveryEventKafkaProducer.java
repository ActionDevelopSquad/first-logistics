package com.firstlogistics.deliverservice.infrastructure.messaging.producer;

import com.firstlogistics.deliverservice.application.port.DeliveryEventProducer;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.event.DeliveryCreatedEvent;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.event.DeliveryCreatedSpringEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventKafkaProducer implements DeliveryEventProducer {

	private static final String TOPIC_CREATED = "delivery.created";

	private final KafkaTemplate<String, Object> deliveryKafkaTemplate;
	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 트랜잭션 내부에서 호출된다.
	 * Kafka를 직접 발행하지 않고 Spring 이벤트로 등록해 트랜잭션 커밋 이후에 발행되도록 위임한다.
	 */
	@Override
	public void sendCreated(DeliveryCreatedEvent deliveryCreatedEvent) {
		applicationEventPublisher.publishEvent(new DeliveryCreatedSpringEvent(deliveryCreatedEvent));
		log.debug("DeliveryCreatedSpringEvent 등록 - deliveryId: {}", deliveryCreatedEvent.deliveryId());
	}

	/**
	 * 트랜잭션 커밋 이후에만 실행된다.
	 * DB 커밋이 실패하면 이 메서드는 호출되지 않으므로, 존재하지 않는 배송 데이터에 대한 이벤트 발행을 방지한다.
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleDeliveryCreated(DeliveryCreatedSpringEvent springEvent) {
		DeliveryCreatedEvent event = springEvent.payload();
		deliveryKafkaTemplate.send(TOPIC_CREATED, event.deliveryId().toString(), event);
		log.info("이벤트 발행 - topic: {}, deliveryId: {}", TOPIC_CREATED, event.deliveryId());
	}
}
