package common.jpa.security.aop;

import common.jpa.entity.enums.UserRole;
import common.jpa.entity.exception.AuthErrorCode;
import common.jpa.entity.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    @Before("@annotation(requireRole)")
    public void checkRoles(RequireRole requireRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean authorized = authorities.contains(onlyMaster.value().name());

        if (!authorized) {
            log.warn("권한 부족 - required={}, currentAuthorities={}", onlyMaster.value().name(), authorities);
            throw new AuthException(AuthErrorCode.FORBIDDEN);
        }
    }
}
