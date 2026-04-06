package com.firstlogistics.orderservice.infrastructure.messaging.consumer;

import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.infrastructure.messaging.event.DeliveryCreatedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.producer.OrderEventKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCreatedRecoverer implements ConsumerRecordRecoverer {

    private final OrderEventKafkaProducer orderEventKafkaProducer;

    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception exception) {
        log.error("[Final Failure] 배송 생성 이벤트 처리 실패. OrderId: {}, Offset: {}", record.key(), record.offset());

        if (!(record.value() instanceof DeliveryCreatedEvent event)) {
            sendToDlt(record);
            return;
        }

        if (shouldRollbackOrder(exception)) {
            orderEventKafkaProducer.handleDeliveryLinkFailed(event);
        }

        sendToDlt(record);
    }

    private boolean shouldRollbackOrder(Exception exception) {
        return hasCause(exception, OrderException.class);
    }

    private void sendToDlt(ConsumerRecord<?, ?> record) {
        orderEventKafkaProducer.sendToDeliveryDlt(String.valueOf(record.key()), record.value());
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
