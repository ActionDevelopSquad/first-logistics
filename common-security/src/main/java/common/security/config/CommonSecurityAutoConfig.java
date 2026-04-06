package common.security.config;

import common.security.aop.RoleCheckAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CommonSecurityAutoConfig {

    @Bean
    public RoleCheckAspect requireRoleAspect() {
        return new RoleCheckAspect();
    }
}