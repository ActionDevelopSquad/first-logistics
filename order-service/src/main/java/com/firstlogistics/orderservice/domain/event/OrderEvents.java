package com.firstlogistics.orderservice.domain.event;

import com.firstlogistics.orderservice.domain.entity.Order;

public interface OrderEvents {
    void created(Order order);
}
