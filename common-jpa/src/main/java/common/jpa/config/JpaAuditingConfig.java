package common.jpa.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    @ConditionalOnBean(CurrentAuditorProvider.class)
    public AuditorAware<UUID> auditorProvider(CurrentAuditorProvider currentAuditorProvider) {
        return currentAuditorProvider::getCurrentAuditor;
    }
}