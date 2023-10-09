package com.deft.authservice.configuration;

import com.deft.authservice.service.AuthServiceRedis;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 *
 * Handle HttpRequest here and authenticate it with AuthServiceRedis
 */

@Component
@RequiredArgsConstructor
public class RedisAuthenticationFilter extends OncePerRequestFilter {

    private final AuthServiceRedis authServiceRedis;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        Optional<Authentication> authentication = this.authServiceRedis.authenticate(request);
        authentication.ifPresent(SecurityContextHolder.getContext()::setAuthentication);
        filterChain.doFilter(request, response);
    }
}
