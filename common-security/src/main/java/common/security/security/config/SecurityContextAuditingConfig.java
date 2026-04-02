package common.security.security.config;

import common.jpa.config.CurrentAuditorProvider;
import common.jpa.config.JpaAuditingConfig;
import common.security.entity.exception.AuthException;
import common.security.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.UUID;

@Configuration
@Slf4j
@AutoConfigureBefore(JpaAuditingConfig.class)
public class SecurityContextAuditingConfig implements CurrentAuditorProvider {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        try {
            return Optional.of(SecurityUtils.currentUser().getUserId());
        } catch (AuthException e) {
            log.debug("현재 사용자 정보를 가져올 수 없습니다: {}", e.getMessage());
            return Optional.empty();
        } catch (RuntimeException e) {
            log.error("인증 조회 중 예기치 않은 오류가 발생했습니다.", e);
            throw e;
        }
    }
}