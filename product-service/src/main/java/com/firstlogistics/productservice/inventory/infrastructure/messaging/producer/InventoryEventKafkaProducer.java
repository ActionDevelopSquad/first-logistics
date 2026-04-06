package com.firstlogistics.productservice.inventory.infrastructure.messaging.producer;

import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.InventoryCancelledEvent;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.InventoryConfirmedEvent;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.InventoryReservationFailedEvent;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventKafkaProducer {

    private static final String TOPIC_RESERVED = "inventory.reserved";
    private static final String TOPIC_RESERVATION_FAILED = "inventory.reservation.failed";
    private static final String TOPIC_CONFIRMED = "inventory.confirmed";
    private static final String TOPIC_CANCELLED = "inventory.cancelled";

    private final KafkaTemplate<String, Object> inventoryKafkaTemplate;

    public void publishReserved(InventoryReservedEvent event) {
        inventoryKafkaTemplate.send(TOPIC_RESERVED, event.orderId().toString(), event);
        log.info("이벤트 발행 - topic: {}, orderId: {}", TOPIC_RESERVED, event.orderId());
    }

    public void publishReservationFailed(InventoryReservationFailedEvent event) {
        inventoryKafkaTemplate.send(TOPIC_RESERVATION_FAILED, event.orderId().toString(), event);
        log.warn("이벤트 발행 - topic: {}, orderId: {}, reason: {}", TOPIC_RESERVATION_FAILED, event.orderId(), event.reason());
    }

    public void publishConfirmed(InventoryConfirmedEvent event) {
        inventoryKafkaTemplate.send(TOPIC_CONFIRMED, event.orderId().toString(), event);
        log.info("이벤트 발행 - topic: {}, orderId: {}", TOPIC_CONFIRMED, event.orderId());
    }

    public void publishCancelled(InventoryCancelledEvent event) {
        inventoryKafkaTemplate.send(TOPIC_CANCELLED, event.orderId().toString(), event);
        log.info("이벤트 발행 - topic: {}, orderId: {}", TOPIC_CANCELLED, event.orderId());
    }
}
