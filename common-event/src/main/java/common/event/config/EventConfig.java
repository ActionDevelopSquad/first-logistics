package common.event.config;

import common.event.Events;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@EnableAsync
@Configuration
public class EventConfig {

    @Bean
    public Events events() {
        return new Events(); // static 필드 주입을 위한 빈 등록
    }

    // 비동기 처리 설정
//    @Bean(name = "taskExecutor")
//    public Executor getAsyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(10);
//        executor.setThreadNamePrefix("Async-");
//        executor.initialize();
//        return executor;
//    }
}