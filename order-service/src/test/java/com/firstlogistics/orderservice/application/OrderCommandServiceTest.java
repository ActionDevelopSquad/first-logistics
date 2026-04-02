package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.CreateOrderCommand;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.entity.OrderTestBuilder;
import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import common.event.Events;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderCommandService orderCommandService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        new Events().init(eventPublisher);
    }

    @AfterEach
    void tearDown() {
        new Events().init(null);
    }

    // --- 주문 생성 테스트 ---

    @Test
    @DisplayName("성공: 올바른 주문 생성 요청 시 주문 ID를 반환하고 주문 생성 이벤트를 발행한다")
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
        UUID orderId = orderCommandService.createOrder(command);

        // then
        assertThat(orderId).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    // --- 주문 승인 테스트 ---

    @Test
    @DisplayName("성공: RESERVED(재고 예약 완료) 상태의 주문 승인 시 ACCEPTED로 변경되고 이벤트가 발행된다")
    void acceptOrder_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.RESERVED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));

        // when
        String resultStatus = orderCommandService.acceptOrder(USER_ID, orderId);

        // then
        assertThat(resultStatus).isEqualTo("ACCEPTED");
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    @DisplayName("실패: RESERVED가 아닌 상태에서 승인 시도 시 예외가 발생한다")
    void acceptOrder_Fail_InvalidStatus() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderCommandService.acceptOrder(USER_ID, orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.INVALID_ORDER_STATUS.getMessage());
    }

    @Test
    @DisplayName("실패: 이미 승인된 주문을 다시 승인하면 멱등성 예외가 발생한다")
    void acceptOrder_Fail_AlreadyAccepted() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.ACCEPTED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderCommandService.acceptOrder(USER_ID, orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.ALREADY_ACCEPTED.getMessage());
    }

    // --- 주문 승인 거절 테스트 ---

    @Test
    @DisplayName("성공: 주문 거절 시 상태가 CANCELLED로 변경되고 이벤트가 발행된다")
    void rejectOrder_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));

        // when
        String resultStatus = orderCommandService.rejectOrder(USER_ID, orderId);

        // then
        assertThat(resultStatus).isEqualTo("CANCELLED");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("실패: 이미 취소된 주문을 다시 거절하면 멱등성 예외가 발생한다")
    void rejectOrder_Fail_AlreadyCancelled() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.CANCELLED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderCommandService.rejectOrder(USER_ID, orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.ALREADY_CANCELLED.getMessage());
    }
}