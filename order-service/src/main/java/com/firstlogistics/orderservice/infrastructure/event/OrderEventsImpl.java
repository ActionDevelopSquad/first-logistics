package com.firstlogistics.orderservice.infrastructure.event;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import com.firstlogistics.orderservice.domain.event.OrderEvents;
import common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventsImpl implements OrderEvents {

    @Override
    public void created(Order order) {
        Events.trigger(OrderCreatedEvent.from(order));
    }
}
