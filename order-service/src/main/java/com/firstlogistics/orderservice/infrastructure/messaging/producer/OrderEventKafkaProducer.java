package com.firstlogistics.orderservice.infrastructure.messaging.producer;

import com.firstlogistics.orderservice.application.port.OrderEventKafkaProducerPort;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventKafkaProducer implements OrderEventKafkaProducerPort {

    private static final String TOPIC_CREATED = "order.created";
    private static final String TOPIC_ACCEPTED = "order.accepted";
    private static final String TOPIC_CANCELLED = "order.cancelled";
    private static final String TOPIC_CREATED_DLT = "order.created.DLT";

    private final KafkaTemplate<String, Object> orderKafkaTemplate;

    /**
     * 트랜잭션 커밋 이후 발행 — DB 커밋 실패 시 이벤트 유출 방지
     */
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        orderKafkaTemplate.send(TOPIC_CREATED, event.supplierCompanyId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("이벤트 발행 실패 - topic: {}, orderId: {}", TOPIC_CREATED, event.orderId(), ex);
                    } else {
                        log.info("이벤트 발행 성공 - topic: {}, orderId: {}, partition: {}, offset: {}",
                                TOPIC_CREATED,
                                event.orderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
