package common.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 컨슈머 공통 설정
 * 새 컨슈머 추가 시 이 클래스를 주입받아 commonConsumerProps()로 기본 설정을 재사용하세요.
 * 예시: OrderAcceptedConsumerConfig
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private static final Map<String, Object> BASE_PROPS = Map.of(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false,
            ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000
    );

    /**
     * 모든 컨슈머에 공통 적용되는 기본 props
     * - 자동 커밋 비활성화 (AckMode.MANUAL 사용)
     * - 처리 시간이 길어져도 consumer group에서 제외되지 않도록 MAX_POLL_INTERVAL_MS 설정
     * - KEY/VALUE_DESERIALIZER는 메시지 타입에 따라 각 Consumer에서 개별 설정 필요
     * - 각 Consumer에서 GROUP_ID_CONFIG를 반드시 개별 설정 필요
     *   ex: props.put(ConsumerConfig.GROUP_ID_CONFIG, "delivery-service-group");
     */
    public Map<String, Object> commonConsumerProps() {
        Map<String, Object> props = new HashMap<>(BASE_PROPS);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return props;
    }
}
