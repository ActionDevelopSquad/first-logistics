package common.security.util;

import common.entity.exception.AuthErrorCode;
import common.entity.exception.AuthException;
import common.security.domain.CustomUserDetails;
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