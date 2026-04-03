package common.security.config;

import common.security.filter.InternalAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**",
                                "/swagger-ui/**",
                                "/api/v1/users/login",
                                "/api/v1/users/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new InternalAuthFilter(), AnonymousAuthenticationFilter.class);
        return http.build();
    }
}