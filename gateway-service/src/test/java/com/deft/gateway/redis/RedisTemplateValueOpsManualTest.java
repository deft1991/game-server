package com.deft.gateway.redis;

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
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * @author Sergey Golitsyn
 * created on 16.10.2023
 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GatewayServiceApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RedisTemplateValueOpsManualTest {

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
    public void givenSessionToken_whenSet_thenSet() {

        Mono<Boolean> result = reactiveValueOps.set("123", new SessionToken("123", "1"));

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void givenSessionTokenId_whenGet_thenReturnsSessionToken() {
        Mono<Boolean> result = reactiveValueOps.set("123", new SessionToken("123", "1"));

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        Mono<SessionToken> fetchedSessionToken = reactiveValueOps.get("123");

        StepVerifier.create(fetchedSessionToken)
                .expectNext(new SessionToken("123", "1"))
                .verifyComplete();
    }

    @Test
    public void givenSessionToken_whenSetWithExpiry_thenSetsWithExpiryTime() throws InterruptedException {

        Mono<Boolean> result = reactiveValueOps.set("129", new SessionToken("129", "1"), Duration.ofSeconds(1));

        Mono<SessionToken> fetchedSessionToken = reactiveValueOps.get("129");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        Thread.sleep(2000L);

        StepVerifier.create(fetchedSessionToken)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
