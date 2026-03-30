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
		// DeliveryCreationException: 확정적 생성 실패 → Saga 보상(주문 취소) + DLT
		if (exception instanceof DeliveryCreationException) {
			UUID orderId = record.value() instanceof OrderAcceptedEvent event ? event.orderId() : null;
			if (orderId != null) {
				kafkaTemplate.send(
					"delivery.creation.failed",
					orderId.toString(),
					new DeliveryCreationFailedEvent(orderId, exception.getMessage())
				);
				log.warn("delivery.creation.failed 발행 - orderId: {}, reason: {}", orderId, exception.getMessage());
			} else {
				log.error("orderId 추출 실패 - record value 타입: {}",
					record.value() == null ? "null" : record.value().getClass().getName());
			}
		}
		// 모든 경우 감사 목적 DLT 적재
		// RuntimeException: 재시도 3회 소진 후 여기 도착 (Saga 보상 없음, 운영자 개입)
		kafkaTemplate.send("order.accepted.DLT", String.valueOf(record.key()), record.value());
		log.warn("order.accepted.DLT 적재 - key: {}, exceptionType: {}", record.key(), exception.getClass().getSimpleName());
	}
}
