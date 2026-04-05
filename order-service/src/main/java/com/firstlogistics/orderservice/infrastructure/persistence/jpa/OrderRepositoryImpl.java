package com.firstlogistics.orderservice.infrastructure.persistence.jpa;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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

    @Override
    public boolean existsByIdAndSupplierManagerId(OrderId id, UUID supplierManagerId) {
        return jpaRepository.existsByIdAndSupplierManagerId(id.id(), supplierManagerId);
    }

    @Override
    public boolean existsByIdAndReceiverManagerId(OrderId id, UUID receiverManagerId) {
        return jpaRepository.existsByIdAndReceiverManagerId(id.id(), receiverManagerId);
    }

    @Override
    public boolean existsByIdAndSupplierHubId(OrderId id, UUID hubId) {
        return jpaRepository.existsByIdAndSupplierHubId(id.id(), hubId);
    }

    @Override
    public void deleteById(OrderId id, UUID userId) {
        OrderJpaEntity order = jpaRepository.findById(id.id())
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
        order.softDelete(userId);
    }

}
