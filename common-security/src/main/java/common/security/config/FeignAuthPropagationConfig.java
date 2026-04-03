package common.security.config;

import common.security.domain.CustomUserDetails;
import common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import feign.RequestInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@AutoConfiguration
@Slf4j
public class FeignAuthPropagationConfig {

    @Bean
    public RequestInterceptor userHeaderPropagationInterceptor() {
        return template -> {
            log.info("Feign interceptor invoked");
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
            } catch (Exception e) {
                log.warn("Failed to propagate user headers", e);
            }
        };
    }
}