package common.security.config;

import common.security.aop.RoleCheckAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({SecurityConfig.class, FeignAuthPropagationConfig.class})
public class CommonSecurityAutoConfig {

    @Bean
    public RoleCheckAspect requireRoleAspect() {
        return new RoleCheckAspect();
    }
}
