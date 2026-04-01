package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.CreateOrderCommand;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.event.OrderEvents;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.vo.OrderItemInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderEvents orderEvents;

    @Transactional
    public UUID createOrder(CreateOrderCommand command) {
        // TODO: 나중에 userId와 검증 로직 필요

        List<OrderItemInput> itemInputs = command.items().stream()
                .map(CreateOrderCommand.OrderItemCommand::toDomainInput)
                .toList();

        Order order = Order.create(
                command.supplierCompanyId(),
                command.supplierManagerId(),
                command.receiverCompanyId(),
                command.receiverManagerId(),
                command.roadAddress(),
                command.detailAddress(),
                command.dueDate(),
                command.requestMemo(),
                itemInputs,
                orderEvents
        );

        orderRepository.save(order);

        return order.getId().id();
    }
}
