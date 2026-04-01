package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.CreateOrderCommand;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;

    @Transactional
    public UUID createOrder(CreateOrderCommand command) {
        // TODO: 나중에 userId와 검증 로직 필요

        Order order = Order.create(
                command.supplierCompanyId(),
                command.supplierManagerId(),
                command.receiverCompanyId(),
                command.receiverManagerId(),
                command.roadAddress(),
                command.detailAddress(),
                command.dueDate(),
                command.requestMemo(),
                command.items()
        );

        orderRepository.save(order);

        return order.getId().id();
    }
}
