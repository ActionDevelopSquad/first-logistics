package com.firstlogistics.orderservice.infrastructure.messaging.producer;

import com.firstlogistics.orderservice.application.port.OrderEventProducer;
import com.firstlogistics.orderservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventKafkaProducer implements OrderEventProducer {

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
        // 재고 예약
        // supplierCompanyId(공급 업체 ID)를 키로 사용
        sendWithLogging(TOPIC_CREATED, event.supplierCompanyId().toString(), event, event.orderId());
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderAcceptedEvent(OrderAcceptedEvent event) {
        // 재고 차감 & 배송 생성
        // orderId를 키로 사용
        sendWithLogging(TOPIC_ACCEPTED, event.orderId().toString(), event, event.orderId());
    }

    // 공통 전송 & 로깅 메서드
    private void sendWithLogging(String topic, String key, Object event, UUID orderId) {
        orderKafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("이벤트 발행 실패 - topic: {}, orderId: {}", topic, orderId, ex);
                    } else {
                        log.info("이벤트 발행 성공 - topic: {}, orderId: {}, partition: {}, offset: {}",
                                topic,
                                orderId,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
