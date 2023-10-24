package com.deft.auth.controller.integration;

import com.deft.auth.data.dto.UserLoginDto;
import com.deft.auth.data.dto.UserRegisterDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Sergey Golitsyn
 * created on 23.10.2023
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = RegistrationControllerIntegrationTest.Initializer.class)
@Testcontainers
public class RegistrationControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void startContainers() {
        postgreSQLContainer.start();
    }

    @Test
    void testBasicRegisterUser() {
        // Prepare request parameters
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        UserRegisterDto requestBody = new UserRegisterDto("testUser", "testPassword");
        // Create an HttpEntity with headers
        HttpEntity<UserRegisterDto> requestEntity = new HttpEntity<>(requestBody, headers);


        // Send an HTTP POST request using the restTemplate
        ResponseEntity<String> response = restTemplate.postForEntity("/v1/auth/register", requestEntity, String.class);

        // Assert the response
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testLogin() {
        // Prepare request parameters
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        UserLoginDto requestBody = new UserLoginDto("testUser", "testPassword");
        // Create an HttpEntity with headers
        HttpEntity<UserLoginDto> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send an HTTP POST request using the restTemplate
        ResponseEntity<String> response = restTemplate.postForEntity("/v1/auth/login", requestEntity, String.class);

        // Assert the response
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    // Add more integration test cases

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
