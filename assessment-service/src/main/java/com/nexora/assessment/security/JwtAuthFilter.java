package com.nexora.assessment.security;

import com.nexora.assessment.constants.ServiceConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(ServiceConstants.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(ServiceConstants.BEARER_PREFIX)) {
            String token = authHeader.substring(ServiceConstants.BEARER_PREFIX.length());

            try {
                if (jwtUtil.validateToken(token)) {
                    UUID userId = jwtUtil.extractUserId(token);
                    request.setAttribute("userId", userId);
                }
            } catch (Exception e) {
                log.error("JWT authentication error: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
