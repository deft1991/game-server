package com.deft.authservice.service.impl;

import com.deft.authservice.data.entity.AuthUser;
import com.deft.authservice.data.redis.SessionToken;
import com.deft.authservice.repo.postgres.AuthUserRepository;
import com.deft.authservice.repo.redis.SessionTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceRedis {

    private static final String BEARER_PREFIX = "Bearer ";

    private final SessionTokenRepository sessionTokenRepository;
    private final AuthUserRepository authUserRepository;

    public Optional<Authentication> authenticate(HttpServletRequest request) {
        return extractBearerTokenHeader(request).flatMap(this::lookup);
    }

    private Optional<Authentication> lookup(String token) {
        try {
            Optional<SessionToken> userOptional = sessionTokenRepository.findById(token);
            if (userOptional.isPresent()) {
                SessionToken sessionToken = userOptional.get();
                String userId = sessionToken.getUserId();
                Optional<AuthUser> authUserOptional = authUserRepository.findById(userId);
                if (authUserOptional.isEmpty()){
                    log.warn("User not found with id {}", userId);
                    return Optional.empty();
                }
                Authentication authentication = createAuthentication(sessionToken.getId(), authUserOptional.get());
                return Optional.of(authentication);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Unknown error while trying to look up Redis token", e);
            return Optional.empty();
        }
    }

    private static Optional<String> extractBearerTokenHeader(@NonNull HttpServletRequest request) {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (nonNull(authorization)) {
                if (authorization.startsWith(BEARER_PREFIX)) {
                    String token = authorization.substring(BEARER_PREFIX.length()).trim();
                    if (!token.isBlank()) {
                        return Optional.of(token);
                    }
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("An unknown error occurred while trying to extract bearer token", e);
            return Optional.empty();
        }
    }

    private static Authentication createAuthentication(String token, AuthUser authUser) {
        // The difference between `hasAuthority` and `hasRole` is that the latter uses the `ROLE_` prefix
        List<GrantedAuthority> authorities = authUser.getRoles().stream()
                .distinct()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(toList());
        return new UsernamePasswordAuthenticationToken(nonNull(token) ? token : "N/A", "N/A", authorities);
    }
}
