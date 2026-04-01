package common.jpa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class JpaAuditingConfig {

    private final CurrentAuditorProvider currentAuditorProvider;

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return currentAuditorProvider::getCurrentAuditor;
    }
}