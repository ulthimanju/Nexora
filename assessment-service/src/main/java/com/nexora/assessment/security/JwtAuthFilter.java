package com.nexora.assessment.security;

import com.nexora.assessment.constants.ServiceConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(ServiceConstants.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(ServiceConstants.BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authHeader.substring(ServiceConstants.BEARER_PREFIX.length());

            try {
                Claims claims = jwtUtil.extractClaims(token);
                UUID userId = jwtUtil.extractUserId(claims);
                List<String> roles = jwtUtil.extractRoles(claims);

                List<SimpleGrantedAuthority> authorities = roles == null
                        ? Collections.emptyList()
                        : roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        authorities
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("userId", userId);
            } catch (Exception e) {
                log.error("JWT authentication error: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
