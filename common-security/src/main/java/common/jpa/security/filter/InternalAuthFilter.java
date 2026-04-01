package common.jpa.security.filter;

import common.jpa.entity.enums.UserRole;
import common.jpa.security.domain.CustomUserDetails;
import common.jpa.security.config.SecurityHeader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InternalAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userIdHeader = request.getHeader(SecurityHeader.USER_ID);
        String usernameHeader = request.getHeader(SecurityHeader.USERNAME);
        String roleHeader = request.getHeader(SecurityHeader.USER_ROLE);
        String nameHeader = request.getHeader(SecurityHeader.USER_NAME);

        if (StringUtils.hasText(userIdHeader) &&
            StringUtils.hasText(usernameHeader) &&
            StringUtils.hasText(roleHeader) &&
            StringUtils.hasText(nameHeader) &&
            SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UUID userId = UUID.fromString(userIdHeader);

            List<UserRole> roles = Arrays.stream(roleHeader.split(","))
                    .map(String::trim)
                    .filter(this::isUserRole)
                    .map(UserRole::valueOf)
                    .toList();

            if (!roles.isEmpty()) {
                UserRole primaryRole = roles.getFirst();

                CustomUserDetails principal = new CustomUserDetails(
                        userId,
                        usernameHeader,
                        nameHeader,
                        primaryRole
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isUserRole(String role) {
        return Arrays.stream(UserRole.values())
                .map(Enum::name)
                .anyMatch(role::equals);
    }
}