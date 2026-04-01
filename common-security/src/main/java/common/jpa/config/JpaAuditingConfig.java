package common.jpa.config;

import common.jpa.security.utill.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing
//@Profile("!test")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            try {
                return Optional.of(SecurityUtils.currentUser().getUserId());
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }
}