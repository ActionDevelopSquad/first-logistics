package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.domain.event.DeliveryCancellationFailedEvent;
import com.firstlogistics.deliverservice.domain.event.OrderCancelledEvent;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.DeliveryEventKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledRecoverer implements ConsumerRecordRecoverer {

	private final DeliveryEventKafkaProducer deliveryEventKafkaProducer;

	@Override
	public void accept(ConsumerRecord<?, ?> record, Exception exception) {
		log.error("[배송 취소 실패] orderId: {}, 원인: {}", record.key(), exception.getMessage(), exception);
		if (!(record.value() instanceof OrderCancelledEvent event)) {
			log.error("orderId 추출 실패 - record value 타입: {}",
				record.value() == null ? "null" : record.value().getClass().getName());
			deliveryEventKafkaProducer.handleOrderCancelledDlt(String.valueOf(record.key()), record.value());
			return;
		}

		deliveryEventKafkaProducer.handleDeliveryCancellationFailed(
			DeliveryCancellationFailedEvent.create(event.orderId(), exception.getMessage())
		);
		deliveryEventKafkaProducer.handleOrderCancelledDlt(String.valueOf(record.key()), record.value());
	}
}
