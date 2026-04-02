package common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules() // LocalDateTime 처리를 위한 JavaTimeModule 등 등록
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 날짜 배열화 방지
                // 역직렬화 시 JSON에는 있지만 객체에는 없는 필드가 있을 때 에러 발생 방지
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}