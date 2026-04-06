package com.firstlogistics.orderservice.domain.repository;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.vo.OrderId;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId id);

    boolean existsById(OrderId id);

    void deleteById(OrderId id, UUID userId);
}
