package com.firstlogistics.orderservice.application.port;

import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;

public interface OrderEventKafkaProducerPort {

    void handleOrderCreatedEvent(OrderCreatedEvent event);
}
