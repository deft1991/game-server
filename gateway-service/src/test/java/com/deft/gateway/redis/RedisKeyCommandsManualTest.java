package com.deft.gateway.redis;

import com.deft.gateway.GatewayServiceApplication;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands.SetCommand;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;

/**
 * @author Sergey Golitsyn
 * created on 16.10.2023
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GatewayServiceApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RedisKeyCommandsManualTest {


    @Autowired
    private ReactiveKeyCommands keyCommands;

    @Autowired
    private ReactiveStringCommands stringCommands;

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

    @Test
    public void givenFluxOfKeys_whenPerformOperations_thenPerformOperations() {
        Flux<String> keys = Flux.just("key1", "key2", "key3", "key4");

        Flux<SetCommand> generator = keys.map(String::getBytes)
                .map(ByteBuffer::wrap)
                .map(key -> SetCommand.set(key)
                        .value(key));

        StepVerifier.create(stringCommands.set(generator))
                .expectNextCount(4L)
                .verifyComplete();

        Mono<Long> keyCount = keyCommands.keys(ByteBuffer.wrap("key*".getBytes()))
                .flatMapMany(Flux::fromIterable)
                .count();

        StepVerifier.create(keyCount)
                .expectNext(4L)
                .verifyComplete();

    }
}
