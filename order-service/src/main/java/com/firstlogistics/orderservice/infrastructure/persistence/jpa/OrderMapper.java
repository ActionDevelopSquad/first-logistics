package com.firstlogistics.orderservice.infrastructure.persistence.jpa;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.entity.OrderItem;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    /**
     * Order 도메인 -> OrderJpaEntity
     */
    public OrderJpaEntity toJpaEntity(Order order) {
        if (order == null) return null;

        return new OrderJpaEntity(
                order.getId().id(),
                order.getSupplier().companyId(),
                order.getSupplier().managerId(),
                order.getSupplier().hubId(),
                order.getReceiver().companyId(),
                order.getReceiver().managerId(),
                order.getDeliveryId(),
                order.getDeliveryAddress().roadAddress(),
                order.getDeliveryAddress().detailAddress(),
                order.getTotalAmount().amount(),
                order.getDueDate(),
                order.getRequestMemo(),
                order.getStatus(),
                order.getPreviousStatus(),
                order.getCancelType(),
                order.getOrderItems().stream()
                        .map(this::toItemEntity)
                        .toList(),
                order.getVersion()
        );
    }

    /**
     * OrderJpaEntity -> Order 도메인
     */
    public Order toDomain(OrderJpaEntity entity) {
        if (entity == null) return null;

        // fetch join으로 가져올 때만 주문 상세 가져오기
        List<OrderItem> items = Hibernate.isInitialized(entity.getOrderItems())
                ? entity.getOrderItems().stream().map(this::toItemDomain).toList()
                : List.of(); // 초기화 안됐으면 추가 쿼리 방지

        return Order.reconstitute(
                entity.getId(),
                entity.getSupplierCompanyId(),
                entity.getSupplierManagerId(),
                entity.getSupplierHubId(),
                entity.getReceiverCompanyId(),
                entity.getReceiverManagerId(),
                entity.getDeliveryId(),
                entity.getRoadAddress(),
                entity.getDetailAddress(),
                entity.getTotalAmount(),
                entity.getDueDate(),
                entity.getRequestMemo(),
                entity.getStatus(),
                entity.getPreviousStatus(),
                entity.getCancelType(),
                entity.getCreatedAt(),
                items,
                entity.getVersion()
        );
    }

    private OrderItemJpaEntity toItemEntity(OrderItem item) {
        return new OrderItemJpaEntity(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice().amount(),
                item.getQuantity(),
                item.getSubTotal().amount()
        );
    }

    private OrderItem toItemDomain(OrderItemJpaEntity itemEntity) {
        return OrderItem.reconstitute(
                itemEntity.getId(),
                itemEntity.getProductId(),
                itemEntity.getProductName(),
                itemEntity.getUnitPrice(),
                itemEntity.getQuantity(),
                itemEntity.getSubTotal()
        );
    }
}