package common.security.filter;

import common.security.config.SecurityHeader;
import common.security.domain.CustomUserDetails;
import common.security.entity.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class InternalServiceForwardFilter extends OncePerRequestFilter {

    public static final String ATTR_INTERNAL_SERVICE_REQUEST = "INTERNAL_SERVICE_REQUEST";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String forwardService = request.getHeader(SecurityHeader.FORWARD_SERVICE);

        if (StringUtils.hasText(forwardService)) {
            request.setAttribute(ATTR_INTERNAL_SERVICE_REQUEST, true);
            request.setAttribute(SecurityHeader.FORWARD_SERVICE, forwardService);

            // 인증 컨텍스트가 없으면 내부 서비스 호출용 시스템 인증 세팅
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails systemUser = new CustomUserDetails(
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        forwardService,
                        forwardService,
                        UserRole.MASTER
                );
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(systemUser, null, systemUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
