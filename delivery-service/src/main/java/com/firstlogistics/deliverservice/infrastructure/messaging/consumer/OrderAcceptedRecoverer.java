package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.domain.exception.DeliveryCreationException;
import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event.OrderAcceptedEvent;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.event.DeliveryCreationFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAcceptedRecoverer implements ConsumerRecordRecoverer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void accept(ConsumerRecord<?, ?> record, Exception exception) {
		if (hasCause(exception, DeliveryCreationException.class)) {
			DeliveryCreationException cause = unwrapCause(exception, DeliveryCreationException.class);
			UUID orderId = record.value() instanceof OrderAcceptedEvent event ? event.orderId() : null;
			if (orderId != null) {
				kafkaTemplate.send(
					"delivery.creation.failed",
					orderId.toString(),
					new DeliveryCreationFailedEvent(orderId, cause.getMessage())
				);
				log.warn("delivery.creation.failed 발행 - orderId: {}, reason: {}", orderId, cause.getMessage());
			} else {
				log.error("orderId 추출 실패 - record value 타입: {}",
					record.value() == null ? "null" : record.value().getClass().getName());
			}
		}
		kafkaTemplate.send("order.accepted.DLT", String.valueOf(record.key()), record.value());
		log.warn("order.accepted.DLT 적재 - key: {}, exceptionType: {}", record.key(), exception.getClass().getSimpleName());
	}

	private boolean hasCause(Throwable exception, Class<? extends Throwable> targetType) {
		Throwable cause = exception;
		while (cause != null) {
			if (targetType.isInstance(cause)) return true;
			cause = cause.getCause();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private <T extends Throwable> T unwrapCause(Throwable exception, Class<T> targetType) {
		Throwable cause = exception;
		while (cause != null) {
			if (targetType.isInstance(cause)) return (T) cause;
			cause = cause.getCause();
		}
		throw new IllegalStateException("cause not found: " + targetType.getSimpleName());
	}
}
