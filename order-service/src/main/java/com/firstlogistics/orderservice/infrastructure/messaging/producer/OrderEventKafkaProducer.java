package com.firstlogistics.orderservice.infrastructure.messaging.producer;

import com.firstlogistics.orderservice.application.OrderCommandService;
import com.firstlogistics.orderservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.orderservice.domain.event.OrderCancelledEvent;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.event.DeliveryCreatedEvent;
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
public class OrderEventKafkaProducer {

    private static final String ORDER_CREATED = "order.created";
    private static final String ORDER_ACCEPTED = "order.accepted";
    private static final String ORDER_CANCELLED = "order.cancelled";
    private static final String DELIVERY_CREATED_DLT = "delivery.created.DLT";

    private final KafkaTemplate<String, Object> orderKafkaTemplate;
    private final OrderCommandService orderCommandService;

    /**
     * 트랜잭션 커밋 이후 발행 — DB 커밋 실패 시 이벤트 유출 방지
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        // 재고 예약
        send(ORDER_CREATED, event.supplierCompanyId().toString(), event, event.orderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderAcceptedEvent(OrderAcceptedEvent event) {
        // 재고 차감 & 배송 생성
        // orderId를 키로 사용
        send(ORDER_ACCEPTED, event.orderId().toString(), event, event.orderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderCancelledEvent(OrderCancelledEvent event) {
        // 재고 예약 취소
        send(ORDER_CANCELLED, event.orderId().toString(), event, event.orderId());
    }

    public void sendToDeliveryDlt(String key, Object payload) {
        log.warn("[Kafka DLT] Sending failed delivery event to DLT. Key: {}", key);
        send(DELIVERY_CREATED_DLT, key, payload, null);
    }

    public void compensateDeliveryLinkFailed(DeliveryCreatedEvent originalEvent) {
        UUID orderId = originalEvent.order().orderId();

        // 주문 DB 상태를 CANCELLED로 변경 (실패할 수도 있음)
        try {
            orderCommandService.cancelByDeliveryFailure(orderId);
            log.info("[Saga] 주문 DB 상태 취소 완료: {}", orderId);
        } catch (Exception e) {
            // DB가 죽어서 에러가 나더라도 죽지 않게 함
            log.error("[Critical] DB 상태 변경 실패! 재고 복구는 진행합니다: {}", orderId);
        }

        // 재고 복구 이벤트 발행
        OrderCancelledEvent cancelEvent = new OrderCancelledEvent(
                orderId,
                originalEvent.order().supplierCompanyId(),
                originalEvent.order().items().stream()
                        .map(item -> new OrderCancelledEvent.OrderItemInfo(item.productId(), item.quantity()))
                        .toList(),
                true
        );

        send(ORDER_CANCELLED, orderId.toString(), cancelEvent, orderId);
    }

    // 공통 전송 & 로깅 메서드
    private void send(String topic, String key, Object event, UUID orderId) {
        orderKafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka] Failed to send: topic={}, orderId={}, error={}",
                                topic, orderId, ex.getMessage(), ex);
                    } else {
                        log.info("[Kafka] Sent: topic={}, orderId={}, partition={}, offset={}",
                                topic, orderId,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
