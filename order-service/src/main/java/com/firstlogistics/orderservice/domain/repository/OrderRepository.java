package com.firstlogistics.orderservice.domain.repository;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.vo.OrderId;

import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(OrderId id);
}
