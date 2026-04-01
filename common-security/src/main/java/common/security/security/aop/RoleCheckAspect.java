package common.security.security.aop;

import common.security.entity.enums.UserRole;
import common.security.entity.exception.AuthErrorCode;
import common.security.entity.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Slf4j
public class RoleCheckAspect {

    @Before("@annotation(requireRole)")
    public void checkRoles(RequireRole requireRole) {
        Set<String> authorities = extractAuthorities();

        if (authorities.contains(UserRole.MASTER.name())) {
            return;
        }

        boolean authorized = Arrays.stream(requireRole.value())
                .map(Enum::name)
                .anyMatch(authorities::contains);

        if (!authorized) {
            log.warn("권한 부족 - required={}, currentAuthorities={}", Arrays.toString(requireRole.value()), authorities);
            throw new AuthException(AuthErrorCode.FORBIDDEN);
        }
    }

    @Before("@annotation(onlyMaster)")
    public void onlyMaster(OnlyMaster onlyMaster) {
        Set<String> authorities = extractAuthorities();

        boolean authorized = authorities.contains(UserRole.MASTER.name());

        if (!authorized) {
            log.warn("권한 부족 - required={}, currentAuthorities={}", UserRole.MASTER.name(), authorities);
            throw new AuthException(AuthErrorCode.FORBIDDEN);
        }
    }

    private Set<String> extractAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return authorities;
    }
}
