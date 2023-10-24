package com.deft.auth.service.impl.integration;

import com.deft.auth.data.entity.AuthUser;
import com.deft.auth.data.redis.SessionToken;
import com.deft.auth.repo.postgres.AuthUserRepository;
import com.deft.auth.repo.redis.SessionTokenRepository;
import com.deft.auth.service.impl.AuthServiceRedisImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sergey Golitsyn
 * created on 23.10.2023
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration",
})
@ContextConfiguration(initializers = AuthServiceRedisImplIntegrationTest.Initializer.class)
@Testcontainers
public class AuthServiceRedisImplIntegrationTest {

    @Autowired
    private AuthServiceRedisImpl authService;

    @Autowired
    private SessionTokenRepository sessionTokenRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    // Define a Redis test container
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379);

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @BeforeAll
    static void startContainers() {
        redisContainer.start();
        postgreSQLContainer.start();
    }

    @AfterAll
    public static void stopContainers() {
        redisContainer.stop();
        postgreSQLContainer.stop();
    }

    @Test
    public void testAuthenticate_ValidToken() {
        String validToken = "validToken";
        AuthUser authUser = AuthUser.builder()
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();
        AuthUser save = authUserRepository.save(authUser);
        SessionToken sessionToken = new SessionToken();
        sessionToken.setId(validToken);
        sessionToken.setUserId(save.getId());
        sessionTokenRepository.save(sessionToken);


        Optional<Authentication> authentication = authService.authenticate(createMockHttpServletRequest("Bearer " + validToken));

        assertTrue(authentication.isPresent());
    }

    private HttpServletRequest createMockHttpServletRequest(String authorizationHeader) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, authorizationHeader);
        return request;
    }

    // Add more integration tests for other scenarios
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.data.redis.host=" + redisContainer.getHost(),
                    "spring.data.redis.port=" + redisContainer.getFirstMappedPort()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
