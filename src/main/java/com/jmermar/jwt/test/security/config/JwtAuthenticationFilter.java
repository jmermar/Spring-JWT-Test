package com.jmermar.jwt.test.security.config;

import com.jmermar.jwt.test.security.services.JwtService;
import com.jmermar.jwt.test.security.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws
            ServletException, IOException {
        final var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Invalid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        final var jwt = authHeader.substring(7);

        if (!jwtService.validateJsonToken(jwt)) {
            log.error("Invalid token");
            filterChain.doFilter(request, response);
            return;
        }

        try {

            final var username = jwtService.getUserNameFromJwtToken(jwt);

            final var user = userService.loadUserByUsername(username);

            final var authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());


            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            log.error("Error loading user: " + e.getMessage());
            filterChain.doFilter(request, response);
        }
    }
}
