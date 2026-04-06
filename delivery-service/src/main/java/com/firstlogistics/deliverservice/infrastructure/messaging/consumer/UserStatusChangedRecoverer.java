package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.domain.event.DeliveryManagerCreationFailedEvent;
import com.firstlogistics.deliverservice.domain.event.UserStatusChangedEvent;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.DeliveryEventKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserStatusChangedRecoverer implements ConsumerRecordRecoverer {

	private final DeliveryEventKafkaProducer deliveryEventKafkaProducer;

	@Override
	public void accept(ConsumerRecord<?, ?> record, Exception exception) {
		if (!(record.value() instanceof UserStatusChangedEvent event)) {
			log.error("userId 추출 실패 - record value 타입: {}",
				record.value() == null ? "null" : record.value().getClass().getName());
			deliveryEventKafkaProducer.handleUserStatusChangedDlt(String.valueOf(record.key()), record.value());
			return;
		}

		deliveryEventKafkaProducer.handleDeliveryManagerCreationFailed(
			DeliveryManagerCreationFailedEvent.create(event.userId(), exception.getMessage())
		);
		deliveryEventKafkaProducer.handleUserStatusChangedDlt(String.valueOf(record.key()), record.value());
	}
}
