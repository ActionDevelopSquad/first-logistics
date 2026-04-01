package com.firstlogistics.orderservice.application;

import com.firstlogistics.orderservice.application.dto.CreateOrderCommand;
import com.firstlogistics.orderservice.domain.event.OrderCreatedEvent;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
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

    @Test
    @Rollback(false) // 이벤트핸들러 동작 확인하기 위해 롤백 안함
    @DisplayName("서비스를 통해 주문 생성 시 DB 저장과 이벤트 발행이 연쇄적으로 발생하는지 확인")
    void order_create_service_test() {
        Mockito.when(orderKafkaTemplate.send(anyString(), any(), any()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // given
        CreateOrderCommand command = createTestCommand(); // 테스트 데이터 생성

        // when
        UUID createdOrderId = orderCommandService.createOrder(command);

        // 이벤트 발행 확인
        assertThat(orderRepository.existsById(OrderId.of(createdOrderId))).isTrue();
        assertThat(applicationEvents.stream(OrderCreatedEvent.class).count()).isEqualTo(1);

        // Kafka 전송 시도 검증
        verify(orderKafkaTemplate, times(1)).send(anyString(), any(), any());
    }

    private CreateOrderCommand createTestCommand() {
        return new CreateOrderCommand(
                UUID.randomUUID(),           // supplierCompanyId
                UUID.randomUUID(),           // supplierManagerId
                UUID.randomUUID(),           // receiverCompanyId
                UUID.randomUUID(),           // receiverManagerId
                "서울시 강남구",           // roadAddress
                "00빌딩",                // detailAddress
                LocalDateTime.now().plusDays(1), // dueDate (하루 뒤)
                "문 앞에 놔주세요",             // requestMemo
                List.of(                     // 주문 상품 목록
                        new CreateOrderCommand.OrderItemCommand(
                                UUID.randomUUID(),   // productId
                                "맛있는 사과",         // productName
                                2000L,               // unitPrice
                                5                    // quantity
                        )
                )
        );
    }
}