package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.domain.event.DeliveryCreationFailedEvent;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.DeliveryEventKafkaProducer;
import com.firstlogistics.deliverservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.deliverservice.domain.exception.DeliveryCreationException;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAcceptedRecoverer implements ConsumerRecordRecoverer {

	private final DeliveryEventKafkaProducer deliveryEventKafkaProducer;

	@Override
	public void accept(ConsumerRecord<?, ?> record, Exception exception) {
		handleOrderAccepted(record, exception);
	}

	private void handleOrderAccepted(ConsumerRecord<?, ?> record, Exception exception) {
		if (!shouldFireSaga(exception)) {
			deliveryEventKafkaProducer.handleOrderAcceptedDlt(String.valueOf(record.key()), record.value());
			return;
		}

		if (!(record.value() instanceof OrderAcceptedEvent event)) {
			log.error("orderId 추출 실패 - record value 타입: {}",
				record.value() == null ? "null" : record.value().getClass().getName());
			deliveryEventKafkaProducer.handleOrderAcceptedDlt(String.valueOf(record.key()), record.value());
			return;
		}

		deliveryEventKafkaProducer.handleDeliveryCreationFailed(
			DeliveryCreationFailedEvent.create(event.orderId(), exception.getMessage())
		);
		deliveryEventKafkaProducer.handleOrderAcceptedDlt(String.valueOf(record.key()), record.value());
	}

	private boolean shouldFireSaga(Exception exception) {
		return !hasCause(exception, DeliveryException.class)
			|| hasCause(exception, DeliveryCreationException.class);
	}

	private boolean hasCause(Throwable exception, Class<? extends Throwable> targetType) {
		Throwable cause = exception;
		while (cause != null) {
			if (targetType.isInstance(cause)) return true;
			cause = cause.getCause();
		}
		return false;
	}
}
