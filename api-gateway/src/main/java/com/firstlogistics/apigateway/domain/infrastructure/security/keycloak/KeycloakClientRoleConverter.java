package com.firstlogistics.apigateway.domain.infrastructure.security.keycloak;//package com.firstlogistics.gwservice.infrastructure.security.keycloak;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class KeycloakClientRoleConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

    private static final Set<String> EXCLUDED_ROLES = Set.of(
            "default-roles-codefactory",
            "offline_access",
            "uma_authorization"
    );

    @Override
    public Flux<GrantedAuthority> convert(Jwt jwt) {
        Flux<String> realmRoles = extractRealmRoles(jwt);
        Flux<String> resourceRoles = extractResourceRoles(jwt);

        return Flux.concat(realmRoles, resourceRoles)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .filter(role -> !EXCLUDED_ROLES.contains(role))
                .distinct()
                .map(this::normalizeRole)
                .map(SimpleGrantedAuthority::new);
    }

    private Flux<String> extractRealmRoles(Jwt jwt) {
        Object realmAccessObj = jwt.getClaims().get("realm_access");
        if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) {
            return Flux.empty();
        }

        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof Collection<?> roles)) {
            return Flux.empty();
        }

        return Flux.fromIterable(roles)
                .filter(Objects::nonNull)
                .map(Object::toString);
    }

    private Flux<String> extractResourceRoles(Jwt jwt) {
        Object resourceAccessObj = jwt.getClaims().get("resource_access");
        if (!(resourceAccessObj instanceof Map<?, ?> resourceAccess)) {
            return Flux.empty();
        }

        return Flux.fromIterable(resourceAccess.values())
                .filter(v -> v instanceof Map<?, ?>)
                .cast(Map.class)
                .flatMap(clientMap -> {
                    Object rolesObj = clientMap.get("roles");
                    if (rolesObj instanceof Collection<?> roles) {
                        return Flux.fromIterable(roles);
                    }
                    return Flux.empty();
                })
                .filter(Objects::nonNull)
                .map(Object::toString);
    }

    private String normalizeRole(String role) {
        if (role == null) return "";
        return role.trim();
    }
}