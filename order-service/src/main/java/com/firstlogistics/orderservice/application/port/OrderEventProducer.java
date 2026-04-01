package com.firstlogistics.orderservice.application.port;

import com.firstlogistics.orderservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;

public interface OrderEventProducer {

    void handleOrderCreatedEvent(OrderCreatedEvent event);
    void handleOrderAcceptedEvent(OrderAcceptedEvent event);
}
