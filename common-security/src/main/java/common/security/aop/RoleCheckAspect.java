package common.security.aop;

import common.security.entity.enums.UserRole;
import common.security.entity.exception.AuthErrorCode;
import common.security.entity.exception.AuthException;
import common.security.filter.InternalServiceForwardFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    @Before("@annotation(requireRole)")
    public void checkRoles(RequireRole requireRole) {
        if (isInternalServiceRequest()) {
            return;
        }

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
            log.warn("권한 부족 - required={}, currentAuthorities={}",
                    Arrays.toString(requireRole.value()), authorities);
            throw new AuthException(AuthErrorCode.FORBIDDEN);
        }
    }

    @Before("@annotation(onlyMaster)")
    public void onlyMaster(OnlyMaster onlyMaster) {
        if (isInternalServiceRequest()) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean authorized = authorities.contains(UserRole.MASTER.name());

        if (!authorized) {
            log.warn("권한 부족 - required={}, currentAuthorities={}",
                    UserRole.MASTER.name(), authorities);
            throw new AuthException(AuthErrorCode.FORBIDDEN);
        }
    }

    private boolean isInternalServiceRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return false;
        }

        HttpServletRequest request = attributes.getRequest();
        Object value = request.getAttribute(InternalServiceForwardFilter.ATTR_INTERNAL_SERVICE_REQUEST);

        return Boolean.TRUE.equals(value);
    }
}