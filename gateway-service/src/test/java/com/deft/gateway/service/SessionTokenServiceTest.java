package com.deft.gateway.service;

import com.deft.gateway.BaseTest;
import com.deft.gateway.GatewayServiceApplication;
import com.deft.gateway.data.SessionToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Sergey Golitsyn
 * created on 16.10.2023
 */

@SpringBootTest(
        classes = {GatewayServiceApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class SessionTokenServiceTest extends BaseTest {

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private ReactiveRedisTemplate<String, SessionToken> redisTemplate;

    private ReactiveHashOperations<String, String, String> reactiveHashOperations;

    @BeforeEach
    public void setup() {
        reactiveHashOperations = redisTemplate.opsForHash();
    }

    @Test
    void testGetSessionTokenBlock() {
        String token = "your_token";

        // Initialize the reactiveHashOperations
        reactiveHashOperations = redisTemplate.opsForHash();

        // Store data in the Redis container
        reactiveHashOperations.putAll("session_token:" + token, Map.of(
                "id", "your_id",
                "userId", "your_userId"
        )).block();

        Mono<SessionToken> resultMono = sessionTokenService.getSessionToken(token);
        SessionToken sessionToken = resultMono.block(); // Block to retrieve the result.

        assertNotNull(sessionToken);
        assertEquals("your_id", sessionToken.getId());
        assertEquals("your_userId", sessionToken.getUserId());
    }

    @Test
    void testGetSessionToken() {
        String token = "your_token";

        // Initialize the reactiveHashOperations
        reactiveHashOperations = redisTemplate.opsForHash();

        // Store data in the Redis container
        reactiveHashOperations.putAll("session_token:" + token, Map.of(
                "id", "your_id",
                "userId", "your_userId"
        )).block();

        // Add your assertions here
        StepVerifier.create(sessionTokenService.getSessionToken(token))
                .assertNext(Assertions::assertNotNull)
                .expectComplete()
                .verify();
    }
}
