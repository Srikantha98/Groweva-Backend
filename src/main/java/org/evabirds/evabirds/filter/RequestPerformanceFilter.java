package org.evabirds.evabirds.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestPerformanceFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestPerformanceFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            // Ignore high-frequency health probes in normal logging
            if (!uri.startsWith("/actuator/health")) {
                if (duration > 1000) {
                    log.warn("[SLOW HTTP] {} {} completed with status {} in {} ms", method, uri, status, duration);
                } else {
                    log.info("[HTTP] {} {} completed with status {} in {} ms", method, uri, status, duration);
                }
            }
        }
    }
}
