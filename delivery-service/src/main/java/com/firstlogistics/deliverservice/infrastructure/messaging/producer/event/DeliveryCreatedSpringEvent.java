package com.firstlogistics.deliverservice.infrastructure.messaging.producer.event;

/**
 * 트랜잭션 커밋 이후 Kafka 발행을 보장하기 위한 Spring ApplicationEvent 래퍼.
 * DeliveryCommandService 내 트랜잭션 안에서 publishEvent()로 등록되고,
 * @TransactionalEventListener(AFTER_COMMIT) 핸들러에서 실제 Kafka 발행이 이루어진다.
 */
public record DeliveryCreatedSpringEvent(DeliveryCreatedEvent payload) {
}
