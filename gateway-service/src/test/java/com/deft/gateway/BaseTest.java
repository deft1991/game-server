package com.deft.gateway;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

/**
 * @author Sergey Golitsyn
 * created on 18.10.2023
 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GatewayServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BaseTest {

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
}
