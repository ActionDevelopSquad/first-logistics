package com.firstlogistics.orderservice.domain.repository;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.vo.OrderId;

import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    /**
     * 주문 기본 정보 조회 (주문 상세 포함 X)
     * 상세 조회가 필요하면 findByIdWithItems 사용
     */
    Optional<Order> findById(OrderId id);

    /**
     * 주문과 주문 상세 조회 (Fetch Join)
     */
    Optional<Order> findByIdWithItems(OrderId id);
}
