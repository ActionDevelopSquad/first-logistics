package com.firstlogistics.orderservice.infrastructure.messaging.consumer;

import com.firstlogistics.orderservice.application.OrderCommandService;
import com.firstlogistics.orderservice.infrastructure.messaging.event.InventoryReservationFailedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.event.InventoryReservedEvent;
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

    private final OrderCommandService orderCommandService;

    @KafkaListener(
            topics = INVENTORY_RESERVED,
            groupId = "order-service",
            containerFactory = "inventoryReservedListenerContainerFactory"
    )
    public void onInventoryReserved(InventoryReservedEvent event, Acknowledgment ack) {
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


}
