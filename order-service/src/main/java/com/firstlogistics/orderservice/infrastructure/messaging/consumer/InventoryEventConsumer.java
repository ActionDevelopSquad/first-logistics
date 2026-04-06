package com.firstlogistics.orderservice.infrastructure.messaging.consumer;

import com.firstlogistics.orderservice.application.OrderCommandService;
import com.firstlogistics.orderservice.infrastructure.messaging.event.InventoryReservationFailedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.event.InventoryResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private static final String INVENTORY_RESERVED = "inventory.reserved";
    private static final String INVENTORY_RESERVATION_FAILED = "inventory.reservation.failed";
    private static final String INVENTORY_CONFIRMED = "inventory.confirmed";
    private static final String INVENTORY_RESERVATION_CANCELLED = "inventory.reservation.cancelled";
    private static final String INVENTORY_CANCELLED = "inventory.cancelled";

    private final OrderCommandService orderCommandService;

    @KafkaListener(
            topics = INVENTORY_RESERVED,
            groupId = "order-service",
            containerFactory = "inventoryResultListenerContainerFactory"
    )
    public void onInventoryReserved(InventoryResultEvent event, Acknowledgment ack) {
        log.info("[Kafka] Received: topic={}, orderId={}", INVENTORY_RESERVED, event.orderId());
        orderCommandService.reserve(event.orderId(), true);
        ack.acknowledge();
    }

    @KafkaListener(
            topics = INVENTORY_RESERVATION_FAILED,
            groupId = "order-service",
            containerFactory = "inventoryReservationFailedListenerContainerFactory"
    )
    public void onInventoryReservationFailed(InventoryReservationFailedEvent event, Acknowledgment ack) {
        log.info("[Kafka] Received: topic={}, orderId={}, reason={}", INVENTORY_RESERVATION_FAILED, event.orderId(), event.reason());
        orderCommandService.reserve(event.orderId(), false);
        ack.acknowledge();
    }

    @KafkaListener(
            topics = INVENTORY_CONFIRMED,
            groupId = "order-service",
            containerFactory = "inventoryResultListenerContainerFactory"
    )
    public void onInventoryConfirmed(InventoryResultEvent event, Acknowledgment ack) {
        log.info("[Kafka] Received: topic={}, orderId={}", INVENTORY_CONFIRMED, event.orderId());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = INVENTORY_RESERVATION_CANCELLED,
            groupId = "order-service",
            containerFactory = "inventoryResultListenerContainerFactory"
    )
    public void onInventoryReservationCancelled(InventoryResultEvent event, Acknowledgment ack) {
        log.info("[Kafka] Received: topic={}, orderId={}", INVENTORY_RESERVATION_CANCELLED, event.orderId());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = INVENTORY_CANCELLED,
            groupId = "order-service",
            containerFactory = "inventoryResultListenerContainerFactory"
    )
    public void onInventoryCancelled(InventoryResultEvent event, Acknowledgment ack) {
        log.info("[Kafka] Received: topic={}, orderId={}", INVENTORY_CANCELLED, event.orderId());
        ack.acknowledge();
    }
}
