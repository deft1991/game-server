package com.deft.gateway.redis;

import com.deft.gateway.GatewayServiceApplication;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
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
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GatewayServiceApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RedisTemplateListOpsManualTest {

    private static final String LIST_NAME = "demo_list";

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    private ReactiveListOperations<String, String> reactiveListOps;

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
        reactiveListOps = redisTemplate.opsForList();
    }

    @Test
    public void givenListAndValues_whenLeftPushAndLeftPop_thenLeftPushAndLeftPop() {
        Mono<Long> lPush = reactiveListOps.leftPushAll(LIST_NAME, "first", "second")
                .log("Pushed");

        StepVerifier.create(lPush)
                .expectNext(2L)
                .verifyComplete();

        Mono<String> lPop = reactiveListOps.leftPop(LIST_NAME)
                .log("Popped");

        StepVerifier.create(lPop)
                .expectNext("second")
                .verifyComplete();
    }

}
