package com.firstlogistics.orderservice.infrastructure.messaging.consumer;

import com.firstlogistics.orderservice.application.OrderCommandService;
import com.firstlogistics.orderservice.infrastructure.messaging.event.DeliveryCreatedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.event.DeliveryCreationFailedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.event.DeliveryStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventConsumer {

    private static final String DELIVERY_CREATED = "delivery.created";
    private static final String DELIVERY_CREATION_FAILED = "delivery.creation.failed";
    private static final String DELIVERY_STATUS_CHANGED = "delivery.status.changed";

    private final OrderCommandService orderCommandService;

    // 배송 생성 성공 시
    @KafkaListener(
            topics = DELIVERY_CREATED,
            groupId = "order-service",
            containerFactory = "deliveryCreatedListenerContainerFactory"
    )
    public void onDeliveryCreated(DeliveryCreatedEvent event, Acknowledgment ack) {
        log.info("[Kafka] Received: topic={}, orderId={}, deliveryId={}", DELIVERY_CREATED, event.order().orderId(), event.delivery().deliveryId());
        orderCommandService.assignDelivery(event.order().orderId(), event.delivery().deliveryId());
        ack.acknowledge();
    }

    // 배송 생성 실패 시
    @KafkaListener(
            topics = DELIVERY_CREATION_FAILED,
            groupId = "order-service",
            containerFactory = "deliveryFailedListenerContainerFactory"
    )
    public void onDeliveryCreationFailed(DeliveryCreationFailedEvent event, Acknowledgment ack) {
        log.info("[Kafka] Received: topic={}, orderId={}, reason={}", DELIVERY_CREATION_FAILED, event.orderId(), event.reason());
        orderCommandService.cancelByDeliveryFailure(event.orderId());
        ack.acknowledge();
    }

    // 배송 상태 변경 시
    @KafkaListener(
            topics = DELIVERY_STATUS_CHANGED,
            groupId = "order-service",
            containerFactory = "deliveryStatusChangedListenerContainerFactory"
    )
    public void onDeliveryStatusChanged(DeliveryStatusChangedEvent event, Acknowledgment ack) {
        log.info("[Kafka] Received: topic={}, orderId={}, status={}", DELIVERY_STATUS_CHANGED, event.orderId(), event.status().name());

        if (event.status() == DeliveryStatusChangedEvent.DeliveryStatus.HUB_WAITING) {
            orderCommandService.startShipping(event.orderId());
        } else if (event.status() == DeliveryStatusChangedEvent.DeliveryStatus.COMPLETED) {
            orderCommandService.complete(event.orderId());
        }

        ack.acknowledge();
    }
}