package com.firstlogistics.orderservice.infrastructure.persistence.jpa;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({OrderMapper.class})
class OrderMapperTest {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TestEntityManager em;

    private UUID orderId;

    @BeforeEach
    void setUp() {
        // 1. 주문 ID 미리 생성
        orderId = UUID.randomUUID();

        // 2. 주문 본체 생성 (Long 타입 가격 반영)
        OrderJpaEntity entity = new OrderJpaEntity(
                orderId,
                UUID.randomUUID(), // supplierCompanyId
                UUID.randomUUID(), // supplierManagerId
                UUID.randomUUID(), // receiverCompanyId
                UUID.randomUUID(), // receiverManagerId
                UUID.randomUUID(), // deliveryId
                "배송 주소",
                30000L,            // totalAmount
                LocalDateTime.now().plusDays(3),
                "배송 메시지",
                OrderStatus.PENDING,
                null,
                new ArrayList<>()
        );

        // 3. 주문 아이템 생성 및 추가 (Long 타입 가격 반영)
        // OrderItemJpaEntity(id, order, productId, productName, unitPrice, quantity, subTotal)
        OrderItemJpaEntity item1 = new OrderItemJpaEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "테스트 상품 1",
                10000L,
                1,
                10000L
        );

        OrderItemJpaEntity item2 = new OrderItemJpaEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "테스트 상품 2",
                20000L,
                1,
                20000L
        );

        // 리스트에 추가
        entity.getOrderItems().add(item1);
        entity.getOrderItems().add(item2);

        // 4. 저장 및 영속성 컨텍스트 비우기
        orderJpaRepository.save(entity);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("목록 조회 시 아이템은 빈 리스트여야 한다 (Lazy)")
    void toDomain_WithLazyLoading() {
        // given
        OrderJpaEntity lazyOrder = orderJpaRepository.findAll().getFirst();

        // when
        Order domain = orderMapper.toDomain(lazyOrder);

        // then
        // Hibernate 프록시 상태인지 확인
        assertThat(Hibernate.isInitialized(lazyOrder.getOrderItems())).isFalse();
        // 매퍼가 빈 리스트를 반환했는지 확인
        assertThat(domain.getOrderItems()).isEmpty();
    }

    @Test
    @DisplayName("주문 단건 조회 시 아이템이 채워져야 한다")
    void toDomain_WithFetchJoin() {
        // given
        // JpaRepository에 작성한 @EntityGraph 메서드 호출
        OrderJpaEntity fetchOrder = orderJpaRepository.findById(orderId).orElseThrow();

        // when
        Order domain = orderMapper.toDomain(fetchOrder);

        // then
        // 초기화 상태 확인
        assertThat(Hibernate.isInitialized(fetchOrder.getOrderItems())).isTrue();
        // 아이템 2개가 잘 매핑되었는지 확인
        assertThat(domain.getOrderItems()).hasSize(2);
        assertThat(domain.getOrderItems().get(0).getProductName()).contains("테스트 상품");
    }
}