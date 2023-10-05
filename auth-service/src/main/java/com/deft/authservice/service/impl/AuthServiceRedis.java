package com.deft.authservice.service.impl;

import com.deft.authservice.data.redis.UserRedis;
import com.deft.authservice.repo.redis.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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

    private final RedisTemplate<String, String> redis;
    private final UserRepository userRepository;

    public Optional<Authentication> authenticate(HttpServletRequest request) {
        return extractBearerTokenHeader(request).flatMap(this::lookup);
    }

    private Optional<Authentication> lookup(String token) {
        try {
            Optional<UserRedis> userOptional = userRepository.findById(token);
            if (userOptional.isPresent()) {
                UserRedis userRedis = userOptional.get();
                Authentication authentication = createAuthentication(userRedis.getId(), userRedis.getRoles());
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

    private static Authentication createAuthentication(String actor, @NonNull List<String> roles) {
        // The difference between `hasAuthority` and `hasRole` is that the latter uses the `ROLE_` prefix
        List<GrantedAuthority> authorities = roles.stream()
                .distinct()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(toList());
        return new UsernamePasswordAuthenticationToken(nonNull(actor) ? actor : "N/A", "N/A", authorities);
    }
}
