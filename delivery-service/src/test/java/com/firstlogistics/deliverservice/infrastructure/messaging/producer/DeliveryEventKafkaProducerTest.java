package com.firstlogistics.deliverservice.infrastructure.messaging.producer;


import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.infrastructure.feign.CompanyClient;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import com.firstlogistics.deliverservice.infrastructure.feign.UserClient;
import com.firstlogistics.deliverservice.infrastructure.messaging.config.KafkaConsumerConfig;
import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event.OrderAcceptedEvent;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.event.DeliveryCreatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.redisson.api.RedissonClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@EmbeddedKafka(
    partitions = 1,
    topics = {"delivery.created"},
    bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class DeliveryEventKafkaProducerTest {

    @Autowired
    private DeliveryEventKafkaProducer producer;

    @Autowired
    private KafkaConsumerConfig kafkaConsumerConfig;

    @MockitoBean
    private RedissonClient redissonClient;
    @MockitoBean
    private CompanyClient companyClient;
    @MockitoBean
    private HubClient hubClient;
    @MockitoBean
    private UserClient userClient;

    @Test
    @DisplayName("sendCreated - delivery.created 토픽에 올바른 키/값으로 메시지 발행")
    void sendCreated_publishes_to_kafka() {

        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        String receiverSlackId = "U12345678";
        DeliveryCreatedEvent deliveryCreatedEvent = DeliveryCreatedEvent.create(deliveryId, orderId, receiverSlackId);

        try (KafkaConsumer<String, DeliveryCreatedEvent> consumer =
                     createTestConsumer("producer-test")) {

            TopicPartition tp = new TopicPartition("delivery.created", 0);
            consumer.assign(List.of(tp));
            consumer.seekToBeginning(List.of(tp));

            // when
            producer.sendCreated(deliveryCreatedEvent);

            // then
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, DeliveryCreatedEvent> records =
                        consumer.poll(Duration.ofSeconds(1));

                assertThat(records.isEmpty()).isFalse();

                var record = records.iterator().next();
                var event = record.value();

                assertThat(record.key()).isEqualTo(deliveryId.toString());
                assertThat(event.deliveryId()).isEqualTo(deliveryId);
                assertThat(event.orderId()).isEqualTo(orderId);
                assertThat(event.receiverSlackId()).isEqualTo(receiverSlackId);
            });
        }
    }

    private KafkaConsumer<String, DeliveryCreatedEvent> createTestConsumer(String groupIdPrefix) {
        Map<String, Object> props = new HashMap<>(kafkaConsumerConfig.commonConsumerProps());
        props.put("group.id", groupIdPrefix + "-" + UUID.randomUUID());
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", JsonDeserializer.class.getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, DeliveryCreatedEvent.class);
        props.put("spring.json.trusted.packages", "*");

        return new KafkaConsumer<>(props);
    }
}