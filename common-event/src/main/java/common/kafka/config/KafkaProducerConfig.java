package common.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private static final Map<String, Object> BASE_PROPS = Map.of(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
            ProducerConfig.ACKS_CONFIG, "all",
            ProducerConfig.RETRIES_CONFIG, 3,
            ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000,
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true,
            ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5,
            ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000
    );

    public Map<String, Object> commonProducerProps() {
        Map<String, Object> props = new HashMap<>(BASE_PROPS);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return props;
    }
}