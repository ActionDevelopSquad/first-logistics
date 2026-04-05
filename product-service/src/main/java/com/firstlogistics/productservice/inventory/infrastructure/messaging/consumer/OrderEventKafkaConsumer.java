package com.firstlogistics.productservice.inventory.infrastructure.messaging.consumer;

import com.firstlogistics.productservice.inventory.application.InventoryCommandService;
import com.firstlogistics.productservice.inventory.application.InventoryCommandService.InventoryItem;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryException;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.InventoryReservationFailedEvent;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.InventoryReservedEvent;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.OrderAcceptedEvent;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.OrderCancelledEvent;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.OrderCreatedEvent;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.producer.InventoryEventKafkaProducer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventKafkaConsumer {

    private final InventoryCommandService inventoryCommandService;
    private final InventoryEventKafkaProducer inventoryEventKafkaProducer;

    @KafkaListener(
            topics = "order.created",
            groupId = "product-service",
            containerFactory = "orderCreatedListenerContainerFactory"
    )
    public void handleOrderCreated(OrderCreatedEvent event, Acknowledgment ack) {
        log.info("order.created 이벤트 수신 - orderId: {}", event.orderId());

        List<InventoryItem> items = event.orderItems().stream()
                .map(item -> new InventoryItem(item.productId(), item.quantity()))
                .toList();

        try {
            inventoryCommandService.reserve(items);
            inventoryEventKafkaProducer.publishReserved(new InventoryReservedEvent(event.orderId()));
        } catch (InventoryException e) {
            log.warn("재고 예약 실패 - orderId: {}, reason: {}", event.orderId(), e.getMessage());
            inventoryEventKafkaProducer.publishReservationFailed(
                    new InventoryReservationFailedEvent(event.orderId(), e.getMessage()));
        }

        ack.acknowledge();
    }

    @KafkaListener(
            topics = "order.accepted",
            groupId = "product-service",
            containerFactory = "orderAcceptedListenerContainerFactory"
    )
    public void handleOrderAccepted(OrderAcceptedEvent event, Acknowledgment ack) {
        log.info("order.accepted 이벤트 수신 - orderId: {}", event.orderId());

        List<InventoryItem> items = event.orderItems().stream()
                .map(item -> new InventoryItem(item.productId(), item.quantity()))
                .toList();

        try {
            inventoryCommandService.confirm(items);
        } catch (InventoryException e) {
            log.error("재고 확정 실패 - orderId: {}, reason: {}", event.orderId(), e.getMessage());
        }

        ack.acknowledge();
    }

    @KafkaListener(
            topics = "order.cancelled",
            groupId = "product-service",
            containerFactory = "orderCancelledListenerContainerFactory"
    )
    public void handleOrderCancelled(OrderCancelledEvent event, Acknowledgment ack) {
        log.info("order.cancelled 이벤트 수신 - orderId: {}", event.orderId());

        List<InventoryItem> items = event.orderItems().stream()
                .map(item -> new InventoryItem(item.productId(), item.quantity()))
                .toList();

        try {
            inventoryCommandService.cancel(items);
        } catch (InventoryException e) {
            log.error("재고 취소 실패 - orderId: {}, reason: {}", event.orderId(), e.getMessage());
        }

        ack.acknowledge();
    }
}
