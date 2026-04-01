package common.security.security.util;

import common.security.entity.exception.AuthErrorCode;
import common.security.entity.exception.AuthException;
import common.security.security.domain.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static CustomUserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
        return principal;
    }
}