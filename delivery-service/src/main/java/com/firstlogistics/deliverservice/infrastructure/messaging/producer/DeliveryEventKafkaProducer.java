package com.firstlogistics.deliverservice.infrastructure.messaging.producer;

import com.firstlogistics.deliverservice.application.port.DeliveryEventProducer;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.event.DeliveryCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventKafkaProducer implements DeliveryEventProducer {

	private static final String TOPIC_CREATED = "delivery.created";

	private final KafkaTemplate<String, Object> deliveryKafkaTemplate;

	@Override
	public void sendCreated(DeliveryCreatedEvent deliveryCreatedEvent) {

		deliveryKafkaTemplate.send(TOPIC_CREATED, deliveryCreatedEvent.deliveryId().toString(), deliveryCreatedEvent);
		log.info("이벤트 발행 - topic: {}, deliveryId: {}", TOPIC_CREATED, deliveryCreatedEvent.deliveryId());
	}
}
