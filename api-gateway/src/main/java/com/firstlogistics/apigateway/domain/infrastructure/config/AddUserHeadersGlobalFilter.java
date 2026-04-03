package com.firstlogistics.apigateway.domain.infrastructure.config;

import com.firstlogistics.apigateway.domain.enums.UserRole;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AddUserHeadersGlobalFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_ROLES = "X-User-Role";

    private static final Set<String> USER_ROLES = Arrays.stream(UserRole.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 5;
//        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx == null ? null : ctx.getAuthentication())
                .flatMap(auth -> {
                    if (!(auth instanceof JwtAuthenticationToken)) {
                        // 인증이 없거나 Jwt가 아니면 그냥 통과
                        return chain.filter(exchange);
                    }

                    Jwt jwt = ((JwtAuthenticationToken) auth).getToken();

                    String role = extractBusinessRoles(jwt);
                    String username = getSafeClaim(jwt, "preferred_username");
                    String name = buildUserName(jwt);

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header(HEADER_USER_ID, Optional.ofNullable(jwt.getSubject()).orElse(""))
                            .header(HEADER_USERNAME, username)
                            .header(HEADER_USER_NAME, name)
                            .header(HEADER_ROLES, role)
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange))
                .onErrorResume(ex -> chain.filter(exchange));
    }

    private String extractBusinessRoles(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();

        extractRealmRoles(jwt, roles);
        extractResourceRoles(jwt, roles);

        return roles.stream()
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .filter(USER_ROLES::contains)
                .findFirst()
                .orElse(null);
    }

    private void extractRealmRoles(Jwt jwt, Set<String> roles) {
        Object claim = jwt.getClaim("realm_access");
        if (claim instanceof Map<?, ?> realmAccess) {
            Object realmRoles = realmAccess.get("roles");
            if (realmRoles instanceof Collection<?> collection) {
                collection.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .forEach(roles::add);
            }
        }
    }

    private void extractResourceRoles(Jwt jwt, Set<String> roles) {
        Object claim = jwt.getClaim("resource_access");
        if (claim instanceof Map<?, ?> resourceAccess) {
            resourceAccess.values().stream()
                    .filter(v -> v instanceof Map<?, ?>)
                    .map(v -> (Map<?, ?>) v)
                    .forEach(clientMap -> {
                        Object clientRoles = clientMap.get("roles");
                        if (clientRoles instanceof Collection<?> collection) {
                            collection.stream()
                                    .filter(Objects::nonNull)
                                    .map(Object::toString)
                                    .forEach(roles::add);
                        }
                    });
        }
    }

    private String getSafeClaim(Jwt jwt, String claimName) {
        String value = jwt.getClaimAsString(claimName);
        return value != null ? value : "";
    }

    private String buildUserName(Jwt jwt) {
        String familyName = getSafeClaim(jwt, "family_name");
        String givenName = getSafeClaim(jwt, "given_name");
        return (familyName + givenName).trim();
    }
}
