package common.security.config;

import common.security.filter.InternalAuthFilter;
import common.security.filter.InternalServiceForwardFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/users/login",
                                "/api/v1/users/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new InternalServiceForwardFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new InternalAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}