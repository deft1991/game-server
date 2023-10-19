package com.deft.gateway.service;

import com.deft.gateway.data.SessionToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author Sergey Golitsyn
 * created on 14.10.2023
 */

@Service
@RequiredArgsConstructor
public class SessionTokenService {

    private final ReactiveRedisTemplate<String, SessionToken> redisTemplate;
    private ReactiveHashOperations<String, String, String> reactiveHashOperations;

    @PostConstruct
    public void setup() {
        reactiveHashOperations = redisTemplate.opsForHash();
    }

    public Mono<SessionToken> getSessionToken(String token) {
        return reactiveHashOperations.entries("session_token:" + token)
                .collect(SessionToken::new, (sessionToken, pair) -> {
                    String field = pair.getKey();
                    String value = pair.getValue();
                    SessionToken.deserializeField(sessionToken, field, value);
                });
    }

    public Mono<Boolean> hasKey(String token) {
        return redisTemplate.hasKey(token);
    }

}
