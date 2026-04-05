package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.command.CreateOrderCommand;
import com.firstlogistics.orderservice.application.port.CompanyPort;
import com.firstlogistics.orderservice.application.port.HubPort;
import com.firstlogistics.orderservice.application.port.UserContextPort;
import com.firstlogistics.orderservice.application.port.dto.CompanyResponse;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.entity.OrderTestBuilder;
import com.firstlogistics.orderservice.domain.enums.OrderCancelType;
import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.orderservice.domain.event.OrderCancelledEvent;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.application.port.OrderAuthorityCheckPort;
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

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderCommandService orderCommandService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private CompanyPort companyPort;

    @Mock
    private OrderAuthorityCheckPort authorityCheck;

    @Mock
    private HubPort hubPort;

    @Mock
    private UserContextPort userContext;

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
        UUID supplierCompanyId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();

        CreateOrderCommand command = createTestCommand(supplierCompanyId);

        when(companyPort.getCompanyById(supplierCompanyId))
                .thenReturn(new CompanyResponse(supplierCompanyId, hubId, UUID.randomUUID()));

        // when
        UUID resultId = orderCommandService.createOrder(command);

        // then
        assertThat(resultId).isNotNull();
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    @DisplayName("실패: 주문 상품이 없는 경우 예외가 발생한다")
    void createOrder_Fail_NoItems() {
        // given
        UUID supplierCompanyId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                supplierCompanyId, UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(),
                "배송 주소", "상세 주소",
                LocalDateTime.now().plusDays(1),
                "메모", List.of() // 빈 리스트
        );

        when(companyPort.getCompanyById(supplierCompanyId))
                .thenReturn(new CompanyResponse(supplierCompanyId, UUID.randomUUID(), UUID.randomUUID()));

        // when & then
        assertThatThrownBy(() -> orderCommandService.createOrder(command))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.ORDER_ITEM_NOT_EXIST.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishEvent(any());
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
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when
        String resultStatus = orderCommandService.acceptOrder(orderId);

        // then
        assertThat(resultStatus).isEqualTo("ACCEPTED");
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishEvent(any(OrderAcceptedEvent.class));
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
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> orderCommandService.acceptOrder(orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.INVALID_ORDER_STATUS_CHANGE.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishEvent(any());
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
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> orderCommandService.acceptOrder(orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.ALREADY_ACCEPTED.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("실패: 권한이 없는 사용자가 승인 시도 시 예외가 발생한다")
    void acceptOrder_Fail_Unauthorized() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.RESERVED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        // 권한 체크 실패 설정
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> orderCommandService.acceptOrder(orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.UNAUTHORIZED_ACCESS.getMessage());

        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishEvent(any(OrderCancelledEvent.class));
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
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when
        String resultStatus = orderCommandService.rejectOrder(orderId);

        // then
        assertThat(resultStatus).isEqualTo("CANCELLED");
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class));
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
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> orderCommandService.rejectOrder(orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.ALREADY_CANCELLED.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    // --- 주문 취소 및 취소 요청 테스트 ---

    @Test
    @DisplayName("성공: 관리자가 직접 주문을 취소하면 CANCELLED 상태가 되고 ADMIN_CANCEL 사유가 저장된다")
    void cancelOrder_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.RESERVED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when
        String resultStatus = orderCommandService.cancelOrder(orderId);

        // then
        assertThat(resultStatus).isEqualTo("CANCELLED");
        assertThat(order.getCancelType()).isEqualTo(OrderCancelType.ADMIN_CANCEL);
        verify(orderRepository).save(order);
        verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class));
    }

    @Test
    @DisplayName("성공: 발주처가 취소 요청 시 CANCEL_REQUESTED 상태가 된다")
    void requestCancel_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.RESERVED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        when(authorityCheck.canRequestCancel(any(), any())).thenReturn(true);

        // when
        String resultStatus = orderCommandService.requestCancel(orderId);

        // then
        assertThat(resultStatus).isEqualTo("CANCEL_REQUESTED");
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("성공: 취소 요청을 승인하면 CANCELLED 상태가 되고 ORDERER_REQUEST 사유가 저장된다")
    void approveCancelRequest_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        // 취소 요청 상태의 주문 준비
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.CANCEL_REQUESTED)
                .previousStatus(OrderStatus.RESERVED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when
        String resultStatus = orderCommandService.approveCancelRequest(orderId);

        // then
        assertThat(resultStatus).isEqualTo("CANCELLED");
        assertThat(order.getCancelType()).isEqualTo(OrderCancelType.ORDERER_REQUEST);
        verify(orderRepository).save(order);
        verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class));
    }

    @Test
    @DisplayName("성공: 취소 요청을 반려하면 이전 상태로 복구된다")
    void rejectCancelRequest_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.CANCEL_REQUESTED)
                .previousStatus(OrderStatus.RESERVED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when
        String resultStatus = orderCommandService.rejectCancelRequest(orderId);

        // then
        assertThat(resultStatus).isEqualTo("RESERVED");
        assertThat(order.getPreviousStatus()).isNull();
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("실패: 취소 요청 상태가 아닌 주문을 반려하려 하면 예외가 발생한다")
    void rejectCancelRequest_Fail_InvalidStatus() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.builder()
                .id(orderId)
                .status(OrderStatus.ACCEPTED)
                .build();

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        when(authorityCheck.canAcceptOrCancel(any(), any(), any(), any())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> orderCommandService.rejectCancelRequest(orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.CANNOT_REJECT_CANCEL.getMessage());
    }

    private CreateOrderCommand createTestCommand(UUID supplierId) {
        CreateOrderCommand.OrderItemCommand item = new CreateOrderCommand.OrderItemCommand(
                UUID.randomUUID(), // productId
                "테스트 상품",      // productName
                10000L,           // price
                2                 // quantity
        );

        return new CreateOrderCommand(
                supplierId,          // 공급 업체 ID
                UUID.randomUUID(),   // 공급 업체 매니저 ID
                UUID.randomUUID(),         // 수령 업체 ID
                UUID.randomUUID(),   // 수령 업체 매니저 ID
                "배송 주소",
                "상세 주소",
                LocalDateTime.now().plusDays(1), // 배송 희망일
                "요청 메모", // 요청 메모
                List.of(item)        // 상품 리스트
        );
    }
}