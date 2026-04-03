package com.nexora.ai.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class InternalKeyFilter extends OncePerRequestFilter {

    @Value("${ai.internal.secret-key}")
    private String internalSecretKey;

    private static final String INTERNAL_KEY_HEADER = "X-Internal-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip for health and actuator endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/actuator/") || path.equals("/api/v1/ai/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedKey = request.getHeader(INTERNAL_KEY_HEADER);

        if (providedKey == null || !providedKey.equals(internalSecretKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing internal API key\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
