package common.security.security.config;

import common.jpa.config.CurrentAuditorProvider;
import common.security.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class SecurityContextAuditingConfig implements CurrentAuditorProvider {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        try {
            return Optional.of(SecurityUtils.currentUser().getUserId());
        } catch (Exception e) {
            log.debug("현재 사용자 정보를 가져올 수 없습니다: {}", e.getMessage());
            return Optional.empty();
        }
    }
}