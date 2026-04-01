package com.firstlogistics.orderservice.infrastructure.persistence.jpa;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = orderMapper.toJpaEntity(order);
        return orderMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.id())
                .map(orderMapper::toDomain);
    }

    @Override
    public boolean existsById(OrderId id) {
        return jpaRepository.existsById(id.id());
    }

}
