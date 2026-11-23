package org.evabirds.evabirds.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.evabirds.evabirds.constant.SecurityConstants;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = request.getHeader(SecurityConstants.CORRELATION_ID_HEADER);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = request.getHeader(SecurityConstants.REQUEST_ID_HEADER);
        }
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }

        MDC.put(SecurityConstants.MDC_CORRELATION_KEY, correlationId);
        response.setHeader(SecurityConstants.CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(SecurityConstants.MDC_CORRELATION_KEY);
        }
    }
}
