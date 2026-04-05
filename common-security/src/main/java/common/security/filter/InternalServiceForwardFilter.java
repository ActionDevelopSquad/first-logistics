package common.security.filter;

import common.security.config.SecurityHeader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class InternalServiceForwardFilter extends OncePerRequestFilter {

    public static final String ATTR_INTERNAL_SERVICE_REQUEST = "INTERNAL_SERVICE_REQUEST";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String forwardService = request.getHeader(SecurityHeader.FORWARD_SERVICE);

        if (StringUtils.hasText(forwardService)) {
            request.setAttribute(ATTR_INTERNAL_SERVICE_REQUEST, true);
            request.setAttribute(SecurityHeader.FORWARD_SERVICE, forwardService);
        }

        filterChain.doFilter(request, response);
    }
}