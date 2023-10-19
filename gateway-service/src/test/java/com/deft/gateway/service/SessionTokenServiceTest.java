package com.deft.gateway.service;

import com.deft.gateway.GatewayServiceApplication;
import com.deft.gateway.data.SessionToken;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Sergey Golitsyn
 * created on 16.10.2023
 */

@SpringBootTest(
        classes = {GatewayServiceApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class SessionTokenServiceTest {

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private ReactiveRedisTemplate<String, SessionToken> redisTemplate;

    private ReactiveValueOperations<String, SessionToken> reactiveValueOps;

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName
                    .parse("redis:5.0.3-alpine"))
                    .withExposedPorts(6379)
                    .withReuse(true);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @BeforeAll
    public static void init() {
        REDIS_CONTAINER.start();
    }

    @AfterAll
    public static void destroy() {
        REDIS_CONTAINER.stop();
    }

    @BeforeEach
    public void setup() {
        reactiveValueOps = redisTemplate.opsForValue();
    }

    @Test
    public void testGetTokenWithNoSessionToken() {
        Mono<SessionToken> token = sessionTokenService.getSessionToken("token");
        StepVerifier.create(token)
                .verifyComplete();
    }

    @Test
    public void givenSessionTokenId_whenGet_thenReturnsSessionToken() {
        Mono<Boolean> result = reactiveValueOps.set("token", new SessionToken("token", "1"));

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        Mono<SessionToken> fetchedSessionToken = sessionTokenService.getSessionToken("token");

        StepVerifier.create(fetchedSessionToken)
                .expectNext(new SessionToken("token", "1"))
                .verifyComplete();
    }
}
