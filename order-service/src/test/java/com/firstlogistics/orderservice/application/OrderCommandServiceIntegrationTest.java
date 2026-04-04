package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.CreateOrderCommand;
import com.firstlogistics.orderservice.application.port.CompanyPort;
import com.firstlogistics.orderservice.application.port.dto.CompanyResponse;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
class OrderCommandServiceIntegrationTest {

    @Autowired
    private OrderCommandService orderCommandService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @MockitoBean
    private KafkaTemplate<String, Object> orderKafkaTemplate;

    @MockitoBean
    private CompanyPort companyPort;

    @MockitoBean
    private RoleCheck roleCheck;

    @Test
    @Rollback(false) // 이벤트핸들러 동작 확인하기 위해 롤백 안함
    @DisplayName("서비스를 통해 주문 생성 시 DB 저장과 이벤트 발행이 연쇄적으로 발생하는지 확인")
    void order_create_service_test() {
        // given
        UUID supplierId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        CreateOrderCommand command = createTestCommand(supplierId);


        Mockito.when(orderKafkaTemplate.send(anyString(), any(), any()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        Mockito.when(companyPort.getCompanyById(supplierId))
                .thenReturn(new CompanyResponse(supplierId, hubId, UUID.randomUUID()));

        // when
        UUID createdOrderId = orderCommandService.createOrder(command);

        // 이벤트 발행 확인
        assertThat(orderRepository.existsById(OrderId.of(createdOrderId))).isTrue();
        assertThat(applicationEvents.stream(OrderCreatedEvent.class).count()).isEqualTo(1);

        // Kafka 전송 시도 검증
        verify(orderKafkaTemplate, times(1)).send(anyString(), any(), any());
    }

    private CreateOrderCommand createTestCommand(UUID supplierId) {
        return new CreateOrderCommand(
                supplierId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "배송 주소",
                "상세 주소",
                LocalDateTime.now().plusDays(1),
                "문 앞에 놔주세요",
                List.of(
                        new CreateOrderCommand.OrderItemCommand(
                                UUID.randomUUID(), "맛있는 사과", 2000L, 5
                        )
                )
        );
    }
}