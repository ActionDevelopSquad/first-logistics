package com.firstlogistics.orderservice.domain.entity;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.vo.Money;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem {
    private UUID id;
    private UUID productId;
    private String productName;
    private Money unitPrice;
    private int quantity;
    private Money subTotal;

    static OrderItem create(
            UUID productId,
            String productName,
            Long unitPrice,
            int quantity
    ) {
        if (productId == null) throw new OrderException(OrderErrorCode.INVALID_PRODUCT_ID);
        if (productName == null || productName.isBlank()) throw new OrderException(OrderErrorCode.INVALID_PRODUCT_NAME);
        if (quantity <= 0) throw new OrderException(OrderErrorCode.INVALID_QUANTITY);

        Money price = Money.of(unitPrice);

        return new OrderItem(
                UUID.randomUUID(),
                productId,
                productName,
                price,
                quantity,
                price.multiply(quantity)
        );
    }

    /**
     * OrderItemJpaEntity -> OrderItem 변환 시에만 사용
     */
    public static OrderItem reconstitute(
            UUID id,
            UUID productId,
            String productName,
            Long unitPrice,
            int quantity,
            Long subTotal
    ) {
        return new OrderItem(
                id,
                productId,
                productName,
                Money.of(unitPrice),
                quantity,
                Money.of(subTotal)
        );
    }
}
