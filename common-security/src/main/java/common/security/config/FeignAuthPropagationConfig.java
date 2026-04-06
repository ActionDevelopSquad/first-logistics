package common.security.config;

import common.security.domain.CustomUserDetails;
import common.security.entity.exception.AuthException;
import common.security.util.SecurityUtils;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@AutoConfiguration
@Slf4j
public class FeignAuthPropagationConfig {

    @Bean
    public RequestInterceptor userHeaderPropagationInterceptor(@Value("${spring.application.name}") String serviceName) {
        return template -> {
            log.debug("Feign interceptor invoked");
            try {
                CustomUserDetails user = SecurityUtils.currentUser();

                if (user.getUserId() != null) {
                    template.header(SecurityHeader.USER_ID, user.getUserId().toString());
                }
                if (user.getUsername() != null) {
                    template.header(SecurityHeader.USERNAME, user.getUsername());
                }
                if (user.getRole() != null) {
                    template.header(SecurityHeader.USER_ROLE, user.getRole().name());
                }
                if (user.getName() != null && !user.getName().isBlank()) {
                    String encodedName = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);
                    template.header(SecurityHeader.USER_NAME, encodedName);
                }
                template.header(SecurityHeader.FORWARD_SERVICE, serviceName);
            } catch (AuthException e) {
                log.debug("인증 컨텍스트가 없어 사용자 헤더 전파를 건너뜁니다.");
                template.header(SecurityHeader.FORWARD_SERVICE, serviceName);
            } catch (RuntimeException e) {
                log.error("사용자 헤더 전파 중 예기치 않은 오류", e);
                throw e;
            }
        };
    }
}