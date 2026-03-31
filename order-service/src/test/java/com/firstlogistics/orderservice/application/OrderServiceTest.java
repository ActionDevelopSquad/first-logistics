package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.CreateOrderCommand;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("성공: 올바른 주문 생성 요청 시 주문 ID를 반환한다")
    void createOrder_Success() {
        // given
        CreateOrderCommand.OrderItemCommand item = new CreateOrderCommand.OrderItemCommand(
                UUID.randomUUID(), "테스트 상품", 10000L, 2
        );
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(),
                "서울시 강남구", "상세주소", LocalDateTime.now().plusDays(1),
                "빨리 배송해주세요", List.of(item)
        );

        // when
        UUID orderId = orderService.createOrder(command);

        // then
        assertThat(orderId).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("실패: 주문 상품이 없는 경우 예외가 발생한다")
    void createOrder_Fail_NoItems() {
        // given
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(),
                "서울시 강남구", "상세주소", LocalDateTime.now().plusDays(1),
                "메모", List.of() // 빈 리스트
        );

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.ORDER_ITEM_NOT_EXIST.getMessage());
    }
}